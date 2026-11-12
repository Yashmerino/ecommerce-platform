package com.yashmerino.ecommerce.services;

import com.yashmerino.ecommerce.kafka.PaymentEventProducer;
import com.yashmerino.ecommerce.kafka.events.PaymentRequestedEvent;
import com.yashmerino.ecommerce.model.Order;
import com.yashmerino.ecommerce.model.Payment;
import com.yashmerino.ecommerce.model.dto.PaymentDTO;
import com.yashmerino.ecommerce.repositories.OrderRepository;
import com.yashmerino.ecommerce.repositories.PaymentRepository;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentEventProducer paymentEventProducer;

    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(paymentRepository, orderRepository, paymentEventProducer);
    }

    @Test
    void testPaySuccessfully() {
        Long orderId = 1L;
        BigDecimal totalAmount = BigDecimal.valueOf(99.99);
        String stripeToken = "tok_visa";

        Order order = new Order();
        order.setId(orderId);
        order.setTotalAmount(totalAmount);

        Payment payment = new Payment();
        payment.setId(100L);
        payment.setOrder(order);
        payment.setAmount(totalAmount);
        payment.setStatus(PaymentStatus.PENDING);

        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.of(order));
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(payment);

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setStripeToken(stripeToken);
        String idempotencyKey = "test-payment-key";

        paymentService.pay(orderId, paymentDTO, idempotencyKey);

        verify(paymentRepository).saveAndFlush(any(Payment.class));
        
        ArgumentCaptor<PaymentRequestedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentRequestedEvent.class);
        verify(paymentEventProducer).sendPaymentRequested(eventCaptor.capture());

        PaymentRequestedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(100L, capturedEvent.paymentId());
        assertEquals(orderId, capturedEvent.orderId());
        assertEquals(totalAmount, capturedEvent.amount());
        assertEquals(stripeToken, capturedEvent.stripeToken());
        assertEquals(idempotencyKey, capturedEvent.idempotencyKey());
    }

    @Test
    void testPayOrderNotFound() {
        Long orderId = 999L;
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setStripeToken("tok_visa");

        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            paymentService.pay(orderId, paymentDTO, "test-payment-key");
        });

        verify(paymentRepository, never()).save(any());
        verify(paymentEventProducer, never()).sendPaymentRequested(any());
    }

    @Test
    void testDuplicateIdempotencyKeyDoesNotCreateAnotherPayment() {
        Long orderId = 1L;
        String idempotencyKey = "test-payment-key";
        Order order = new Order();
        order.setId(orderId);
        Payment existingPayment = new Payment();
        existingPayment.setOrder(order);
        when(paymentRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(existingPayment));

        paymentService.pay(orderId, new PaymentDTO(), idempotencyKey);

        verify(paymentRepository, never()).saveAndFlush(any(Payment.class));
        verifyNoInteractions(orderRepository, paymentEventProducer);
    }

    @Test
        void testDifferentKeyDoesNotStartPaymentWhileOrderPaymentIsPending() {
        Long orderId = 1L;
        String idempotencyKey = "test-payment-key";
        Order order = new Order();
        order.setId(orderId);
        order.setTotalAmount(BigDecimal.TEN);
        Payment existingPayment = new Payment();
        existingPayment.setOrder(order);
        existingPayment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.findByIdempotencyKey(idempotencyKey))
            .thenReturn(Optional.empty());
        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.of(order));
        when(paymentRepository.findFirstByOrderIdOrderByIdDesc(orderId)).thenReturn(existingPayment);

        paymentService.pay(orderId, new PaymentDTO(), idempotencyKey);

        verify(paymentRepository, never()).saveAndFlush(any(Payment.class));
        verify(paymentEventProducer, never()).sendPaymentRequested(any());
    }
}
