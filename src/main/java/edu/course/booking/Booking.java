package edu.course.booking;

/**
 * A successfully paid booking, handed to {@link IEventRepository#saveBooking(Booking)}.
 *
 * @param customerId         the customer who booked
 * @param eventId            the booked event
 * @param ticketQuantity     number of tickets booked
 * @param totalAmountInCents amount charged (ticketQuantity x pricePerTicketInCents)
 * @param transactionId      transaction id returned by the payment gateway
 */
public record Booking(String customerId,
                      String eventId,
                      int ticketQuantity,
                      long totalAmountInCents,
                      String transactionId) {
}
