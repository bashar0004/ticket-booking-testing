package edu.course.booking;

/** Sends booking confirmations. Replaced by a test double in unit tests. */
public interface INotificationService {

    /**
     * Sends a booking confirmation to the customer.
     *
     * @param customerId    the customer to notify
     * @param eventId       the booked event
     * @param transactionId the payment transaction id
     */
    void sendConfirmation(String customerId, String eventId, String transactionId);
}
