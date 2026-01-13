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

            paymentRepository.save(payment);

            resultProducer.sendSucceeded(event.orderId());
            log.info("Payment with ID {} for order with ID {} was successfully made.", event.paymentId(), event.orderId());
        } catch (Exception e) {
            log.error("Payment with ID {} for order with ID {} couldn't be made.", event.paymentId(), event.orderId(), e);
            paymentRepository.save(
                    new Payment(event.orderId(), null, event.amount(), PaymentStatus.FAILED)
            );

            resultProducer.sendFailed(event.orderId(), e.getMessage());
        }
    }
}
