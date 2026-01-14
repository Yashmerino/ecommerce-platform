package com.yashmerino.ecommerce.service.impl;

import com.yashmerino.ecommerce.kafka.PaymentResultProducer;
import com.yashmerino.ecommerce.kafka.events.PaymentRequestedEvent;
import com.yashmerino.ecommerce.model.Payment;
import com.yashmerino.ecommerce.model.stripe.StripePaymentResult;
import com.yashmerino.ecommerce.repository.PaymentRepository;
import com.yashmerino.ecommerce.service.PaymentService;
import com.yashmerino.ecommerce.service.StripePaymentService;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Payment service implementation.
 */
@Service
@AllArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    /**
     * Stripe payment service.
     */
    private final StripePaymentService stripeService;

    /**
     * Payment repository.
     */
    private final PaymentRepository paymentRepository;

    /**
     * Producer that produces Kafka payment result event.
     */
    private final PaymentResultProducer resultProducer;

    /**
     * Processes the payment.
     *
     * @param event is the event from Kafka topic.
     */
    @Override
    @Transactional
    public void processPayment(PaymentRequestedEvent event) {
        try {
            StripePaymentResult result =
                    stripeService.charge(
                            event.amount(),
                            "EUR",
                            event.stripeToken()
                    );

            Payment payment = new Payment(
                    event.orderId(),
                    result.getPaymentIntentId(),
                    event.amount(),
                    PaymentStatus.SUCCEEDED
            );

            payment = paymentRepository.save(payment);

            // Send back the original paymentId from server, not the new ID from payment-service DB
            resultProducer.sendSucceeded(event.orderId(), event.paymentId());
            log.info("Payment processed successfully for order with ID {} (server payment ID: {})", event.orderId(), event.paymentId());
        } catch (Exception e) {
            log.error("Payment for order with ID {} couldn't be made.", event.orderId(), e);
            Payment failedPayment = paymentRepository.save(
                    new Payment(event.orderId(), null, event.amount(), PaymentStatus.FAILED)
            );

            // Send back the original paymentId from server
            resultProducer.sendFailed(event.orderId(), event.paymentId(), e.getMessage());
        }
    }
}
