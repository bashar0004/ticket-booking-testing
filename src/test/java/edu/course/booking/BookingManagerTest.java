package edu.course.booking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

class BookingManagerTest {

    private static final String CUSTOMER_ID = "C-100";
    private static final String EVENT_ID = "E-200";
    private static final String TRANSACTION_ID = "TX-300";

    private IPaymentGateway paymentGateway;
    private INotificationService notificationService;
    private IEventRepository eventRepository;
    private BookingManager bookingManager;

    @BeforeEach
    void setUp() {
        paymentGateway = mock(IPaymentGateway.class);
        notificationService = mock(INotificationService.class);
        eventRepository = mock(IEventRepository.class);
        bookingManager = new BookingManager(paymentGateway, notificationService, eventRepository);
    }

    // ---------------------------------------------------------------- M1-US1

    @Test
    void successfulBooking_chargesTotal_savesBooking_sendsConfirmation_andReturnsTransactionId() {
        BookingRequest request = new BookingRequest(CUSTOMER_ID, EVENT_ID, 3, 2500);
        when(eventRepository.isSoldOut(EVENT_ID)).thenReturn(false);
        when(paymentGateway.processPayment(CUSTOMER_ID, 7500L)).thenReturn(TRANSACTION_ID);

        BookingResult result = bookingManager.processBooking(request);

        assertNotNull(result);
        assertEquals(BookingResult.Status.SUCCESS, result.status());
        assertEquals(TRANSACTION_ID, result.transactionId());

        InOrder inOrder = inOrder(eventRepository, paymentGateway, notificationService);
        inOrder.verify(eventRepository, times(1)).isSoldOut(EVENT_ID);
        inOrder.verify(paymentGateway, times(1)).processPayment(CUSTOMER_ID, 7500L);
        inOrder.verify(eventRepository, times(1))
                .saveBooking(new Booking(CUSTOMER_ID, EVENT_ID, 3, 7500L, TRANSACTION_ID));
        inOrder.verify(notificationService, times(1))
                .sendConfirmation(CUSTOMER_ID, EVENT_ID, TRANSACTION_ID);
        verifyNoMoreInteractions(eventRepository, paymentGateway, notificationService);
    }
}
