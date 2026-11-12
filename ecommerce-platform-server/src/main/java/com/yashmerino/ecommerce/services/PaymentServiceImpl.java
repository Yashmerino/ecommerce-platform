package com.yashmerino.ecommerce.services;

/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 + MIT License
 +
 + Copyright (c) 2023 Artiom Bozieac
 +
 + Permission is hereby granted, free of charge, to any person obtaining a copy
 + of this software and associated documentation files (the "Software"), to deal
 + in the Software without restriction, including without limitation the rights
 + to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 + copies of the Software, and to permit persons to whom the Software is
 + furnished to do so, subject to the following conditions:
 +
 + The above copyright notice and this permission notice shall be included in all
 + copies or substantial portions of the Software.
 +
 + THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 + IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 + FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 + AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 + LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 + OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 + SOFTWARE.
 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

import com.yashmerino.ecommerce.kafka.PaymentEventProducer;
import com.yashmerino.ecommerce.model.*;
import com.yashmerino.ecommerce.model.dto.PaymentDTO;
import com.yashmerino.ecommerce.kafka.events.PaymentRequestedEvent;
import com.yashmerino.ecommerce.repositories.OrderRepository;
import com.yashmerino.ecommerce.repositories.PaymentRepository;
import com.yashmerino.ecommerce.services.interfaces.PaymentService;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementation for payment service.
 */
@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    /**
     * Payment repository.
     */
    private final PaymentRepository paymentRepository;

    /**
     * Order repository.
     */
    private final OrderRepository orderRepository;

    /**
     * Kafka Payment event producer.
     */
    private final PaymentEventProducer paymentEventProducer;

    /**
     * Sends an event to Kafka topic to process the payment for an order.
     *
     * @param orderId is the payment's order ID.
     * @param paymentDTO is the payment's DTO.
     */
    @Override
    @Transactional
    public void pay(Long orderId, PaymentDTO paymentDTO, String idempotencyKey) {
        Payment existingPayment = paymentRepository.findByIdempotencyKey(idempotencyKey).orElse(null);
        if (existingPayment != null) {
            ensureSameOrder(existingPayment, orderId);
            return;
        }

        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new EntityNotFoundException("order_not_found"));
        Payment latestPayment = paymentRepository.findFirstByOrderIdOrderByIdDesc(orderId);
        if (latestPayment != null && latestPayment.getStatus() != PaymentStatus.FAILED) {
            return;
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setIdempotencyKey(idempotencyKey);
        payment = paymentRepository.saveAndFlush(payment);

        PaymentRequestedEvent event = new PaymentRequestedEvent(
                payment.getId(), orderId, order.getTotalAmount(), paymentDTO.getStripeToken(), idempotencyKey
        );
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    paymentEventProducer.sendPaymentRequested(event);
                }
            });
        } else {
            paymentEventProducer.sendPaymentRequested(event);
        }
    }

    private void ensureSameOrder(Payment payment, Long orderId) {
        if (!payment.getOrder().getId().equals(orderId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "idempotency_key_reused");
        }
    }
}
