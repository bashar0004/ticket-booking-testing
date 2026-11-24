package edu.course.booking;

/**
 * A customer's request to buy tickets for an event.
 *
 * @param customerId            identifies the customer
 * @param eventId               identifies the requested event
 * @param ticketQuantity        number of requested tickets (valid range: 1 to 5)
 * @param pricePerTicketInCents price of one ticket in the smallest currency unit (must be &gt; 0);
 *                              for example 2500 represents 25.00 currency units
 */
public record BookingRequest(String customerId,
                             String eventId,
                             int ticketQuantity,
                             int pricePerTicketInCents) {
}
