package edu.course.booking;

/**
 * Thrown by {@link IPaymentGateway#processPayment(String, long)} when a payment cannot be completed
 * (for example, a declined card). This is the documented way a payment failure is represented.
 */
public class PaymentFailedException extends RuntimeException {

    public PaymentFailedException(String message) {
        super(message);
    }

    public PaymentFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
