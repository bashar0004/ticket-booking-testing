package edu.course.booking;

public class BookingManager {

    static final int MIN_TICKETS = 1;
    static final int MAX_TICKETS = 5;

    private final IPaymentGateway paymentGateway;
    private final INotificationService notificationService;
    private final IEventRepository eventRepository;

    public BookingManager(IPaymentGateway paymentGateway,
                          INotificationService notificationService,
                          IEventRepository eventRepository) {
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
        this.eventRepository = eventRepository;
    }

    public BookingResult processBooking(BookingRequest request) {
        if (!isValid(request)) {
            return BookingResult.invalidRequest();
        }
        eventRepository.isSoldOut(request.eventId()); // result not used yet: no test needs it
        long total = calculateTotal(request);
        String transactionId = paymentGateway.processPayment(request.customerId(), total);
        eventRepository.saveBooking(toBooking(request, total, transactionId));
        notificationService.sendConfirmation(request.customerId(), request.eventId(), transactionId);
        return BookingResult.success(transactionId);
    }

    private static boolean isValid(BookingRequest request) {
        return request != null
                && !isBlank(request.customerId())
                && !isBlank(request.eventId())
                && request.ticketQuantity() >= MIN_TICKETS
                && request.ticketQuantity() <= MAX_TICKETS
                && request.pricePerTicketInCents() > 0;
    }

    private static boolean isBlank(String text) {
        return text == null || text.isBlank();
    }

    private static long calculateTotal(BookingRequest request) {
        return (long) request.ticketQuantity() * request.pricePerTicketInCents();
    }

    private static Booking toBooking(BookingRequest request, long total, String transactionId) {
        return new Booking(request.customerId(), request.eventId(),
                request.ticketQuantity(), total, transactionId);
    }
}
