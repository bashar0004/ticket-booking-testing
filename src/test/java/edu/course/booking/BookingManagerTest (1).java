package edu.course.booking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

    // ---------------------------------------------------------------- M1-US2

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRequests")
    void invalidRequest_isRejectedBeforeAnyDependencyIsUsed(String description, BookingRequest request) {
        BookingResult result = bookingManager.processBooking(request);

        assertNotNull(result);
        assertEquals(BookingResult.Status.INVALID_REQUEST, result.status());
        assertNull(result.transactionId());
        verifyNoInteractions(eventRepository, paymentGateway, notificationService);
    }

    static Stream<Arguments> invalidRequests() {
        return Stream.of(
                Arguments.of("null request", null),
                Arguments.of("null customerId", new BookingRequest(null, EVENT_ID, 2, 2500)),
                Arguments.of("empty customerId", new BookingRequest("", EVENT_ID, 2, 2500)),
                Arguments.of("blank customerId", new BookingRequest("   ", EVENT_ID, 2, 2500)),
                Arguments.of("null eventId", new BookingRequest(CUSTOMER_ID, null, 2, 2500)),
                Arguments.of("empty eventId", new BookingRequest(CUSTOMER_ID, "", 2, 2500)),
                Arguments.of("blank eventId", new BookingRequest(CUSTOMER_ID, "   ", 2, 2500)),
                Arguments.of("quantity 0 (below minimum)", new BookingRequest(CUSTOMER_ID, EVENT_ID, 0, 2500)),
                Arguments.of("quantity 6 (above maximum)", new BookingRequest(CUSTOMER_ID, EVENT_ID, 6, 2500)),
                Arguments.of("negative quantity", new BookingRequest(CUSTOMER_ID, EVENT_ID, -1, 2500)),
                Arguments.of("price 0", new BookingRequest(CUSTOMER_ID, EVENT_ID, 2, 0)),
                Arguments.of("negative price", new BookingRequest(CUSTOMER_ID, EVENT_ID, 2, -100))
        );
    }

    // ---------------------------------------------------------------- M1-US3

    @Test
    void soldOutEvent_returnsSoldOut_andDoesNotPaySaveOrNotify() {
        BookingRequest request = new BookingRequest(CUSTOMER_ID, EVENT_ID, 2, 2500);
        when(eventRepository.isSoldOut(EVENT_ID)).thenReturn(true);

        BookingResult result = bookingManager.processBooking(request);

        assertNotNull(result);
        assertEquals(BookingResult.Status.SOLD_OUT, result.status());
        assertNull(result.transactionId());
        verify(eventRepository, times(1)).isSoldOut(EVENT_ID);
        verify(eventRepository, never()).saveBooking(any());
        verifyNoInteractions(paymentGateway, notificationService);
    }
}
