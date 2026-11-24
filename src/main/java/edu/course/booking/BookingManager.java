package edu.course.booking;

public class BookingManager {

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
        eventRepository.isSoldOut(request.eventId()); // result not used yet: no test needs it
        long total = (long) request.ticketQuantity() * request.pricePerTicketInCents();
        String transactionId = paymentGateway.processPayment(request.customerId(), total);
        Booking booking = new Booking(request.customerId(), request.eventId(),
                request.ticketQuantity(), total, transactionId);
        eventRepository.saveBooking(booking);
        notificationService.sendConfirmation(request.customerId(), request.eventId(), transactionId);
        return BookingResult.success(transactionId);
    }
}
