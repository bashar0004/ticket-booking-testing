package edu.course.booking;

/** Processes payments. Replaced by a test double in unit tests. */
public interface IPaymentGateway {

    /**
     * Charges the customer.
     *
     * @param customerId    the customer to charge
     * @param amountInCents the amount to charge, in the smallest currency unit
     * @return a non-blank transaction id when the payment succeeds
     * @throws PaymentFailedException when the payment fails
     */
    String processPayment(String customerId, long amountInCents);
}
