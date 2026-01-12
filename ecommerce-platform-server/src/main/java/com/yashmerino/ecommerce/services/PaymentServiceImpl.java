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

import com.yashmerino.ecommerce.model.*;
import com.yashmerino.ecommerce.model.dto.PaymentDTO;
import com.yashmerino.ecommerce.model.events.PaymentRequestedEvent;
import com.yashmerino.ecommerce.repositories.OrderRepository;
import com.yashmerino.ecommerce.repositories.PaymentRepository;
import com.yashmerino.ecommerce.services.interfaces.PaymentService;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
    public void pay(Long orderId, PaymentDTO paymentDTO) {
        Order order = this.orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("order_not_found"));

        Payment payment = new Payment(order, order.getTotalAmount(), PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);

        PaymentRequestedEvent event = new PaymentRequestedEvent(payment.getId(), orderId, order.getTotalAmount(), paymentDTO.getStripeToken());
        paymentEventProducer.sendPaymentRequested(event);
    }
}
