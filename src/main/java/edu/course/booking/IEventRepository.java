package edu.course.booking;

/** Event data access. Replaced by a test double in unit tests; no real storage is required. */
public interface IEventRepository {

    /**
     * @param eventId the event to check
     * @return {@code true} when the event is sold out
     */
    boolean isSoldOut(String eventId);

    /**
     * Records a successfully paid booking.
     *
     * @param booking the booking to record
     */
    void saveBooking(Booking booking);
}
