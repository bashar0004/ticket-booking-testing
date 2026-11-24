package edu.course.booking;

/**
 * Outcome of {@link BookingManager#processBooking(BookingRequest)}.
 * Every outcome is reported through a result object; no exception is thrown by the manager
 * for invalid requests, sold-out events or failed payments.
 *
 * @param status        what happened
 * @param transactionId the payment transaction id; non-null only when {@code status} is {@link Status#SUCCESS}
 */
public record BookingResult(Status status, String transactionId) {

    public enum Status {
        SUCCESS,
        INVALID_REQUEST,
        SOLD_OUT,
        PAYMENT_FAILED
    }

    public static BookingResult success(String transactionId) {
        return new BookingResult(Status.SUCCESS, transactionId);
    }

    public static BookingResult invalidRequest() {
        return new BookingResult(Status.INVALID_REQUEST, null);
    }

    public static BookingResult soldOut() {
        return new BookingResult(Status.SOLD_OUT, null);
    }

    public static BookingResult paymentFailed() {
        return new BookingResult(Status.PAYMENT_FAILED, null);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}
