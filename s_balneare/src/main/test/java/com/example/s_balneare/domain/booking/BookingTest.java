package com.example.s_balneare.domain.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {
    private Booking pendingCustomerBooking;
    private Booking confirmedCustomerBooking;
    private BookingParking defaultParking;

    @BeforeEach
    void setUp() {
        defaultParking = new BookingParking(1, 0, 0);

        //prenotazione standard (Customer) in stato PENDING
        pendingCustomerBooking = new Booking(
                1, 10, 100, null, null,
                LocalDate.now().plusDays(5), List.of(1, 2),
                2, 0, 0, 1, defaultParking, 50.0, BookingStatus.PENDING
        );

        //prenotazione standard (Customer) in stato CONFIRMED
        confirmedCustomerBooking = new Booking(
                2, 10, 100, null, null,
                LocalDate.now().plusDays(5), List.of(3),
                0, 0, 0, 0, defaultParking, 20.0, BookingStatus.CONFIRMED
        );
    }

    // ==========================================
    // 1. TEST COSTRUTTORE
    // ==========================================

    @Test
    void constructor_CreatesCustomerBooking_Correctly() {
        Booking b = new Booking(1, 10, 100, null, null, LocalDate.now(), List.of(1), 0, 0, 0, 0, defaultParking, 10.0, null);
        assertEquals(100, b.getCustomerId());
        assertNull(b.getCallerName());
        assertNull(b.getCallerPhone());
        assertEquals(BookingStatus.PENDING, b.getStatus(), "status = PENDING");
    }

    @Test
    void constructor_CreatesManualBooking_Correctly_AndFormatsPhone() {
        Booking b = new Booking(1, 10, null, "Mario Rossi", "+39 333 123 4567", LocalDate.now(), List.of(1), 0, 0, 0, 0, defaultParking, 10.0, BookingStatus.CONFIRMED);
        assertNull(b.getCustomerId());
        assertEquals("Mario Rossi", b.getCallerName());
        assertEquals("+393331234567", b.getCallerPhone(), "numero di telefono ripulito dagli spazi");
    }

    @Test
    void constructor_ThrowsException_IfMissingBothCustomerAndCaller() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Booking(1, 10, null, null, null, LocalDate.now(), List.of(1), 0, 0, 0, 0, defaultParking, 10.0, null)
        );
        assertTrue(ex.getMessage().contains("callerName not valid"));
    }

    @Test
    void constructor_ThrowsException_IfCallerPhoneIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new Booking(1, 10, null, "Mario", "numero-falso", LocalDate.now(), List.of(1), 0, 0, 0, 0, defaultParking, 10.0, null)
        );
    }

    @Test
    void constructor_ThrowsException_IfNegativeValuesPassed() {
        assertThrows(IllegalArgumentException.class, () -> new Booking(1, 10, 100, null, null, LocalDate.now(), List.of(1), -1, 0, 0, 0, defaultParking, 10.0, null));
        assertThrows(IllegalArgumentException.class, () -> new Booking(1, 10, 100, null, null, LocalDate.now(), List.of(1), 0, -1, 0, 0, defaultParking, 10.0, null));
        assertThrows(IllegalArgumentException.class, () -> new Booking(1, 10, 100, null, null, LocalDate.now(), List.of(1), 0, 0, 0, 0, defaultParking, -10.0, null));
    }

    @Test
    void constructor_ThrowsException_IfSpotIdsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new Booking(1, 10, 100, null, null, LocalDate.now(), null, 0, 0, 0, 0, defaultParking, 10.0, null));
        assertThrows(IllegalArgumentException.class, () -> new Booking(1, 10, 100, null, null, LocalDate.now(), List.of(), 0, 0, 0, 0, defaultParking, 10.0, null));
        assertThrows(IllegalArgumentException.class, () -> new Booking(1, 10, 100, null, null, LocalDate.now(), List.of(1, -5), 0, 0, 0, 0, defaultParking, 10.0, null));
    }

    // ==========================================
    // 2. TEST STATUS (update)
    // ==========================================

    @Test
    void confirmBooking_Succeeds_IfPending() {
        pendingCustomerBooking.confirmBooking();
        assertEquals(BookingStatus.CONFIRMED, pendingCustomerBooking.getStatus());
    }

    @Test
    void confirmBooking_ThrowsException_IfNotPending() {
        assertThrows(IllegalStateException.class, () -> confirmedCustomerBooking.confirmBooking());
    }

    @Test
    void rejectBooking_Succeeds_IfPending() {
        pendingCustomerBooking.rejectBooking();
        assertEquals(BookingStatus.REJECTED, pendingCustomerBooking.getStatus());
    }

    @Test
    void rejectBooking_ThrowsException_IfNotPending() {
        assertThrows(IllegalStateException.class, () -> confirmedCustomerBooking.rejectBooking());
    }

    @Test
    void cancelBooking_Succeeds_IfPendingOrConfirmed() {
        pendingCustomerBooking.cancelBooking();
        assertEquals(BookingStatus.CANCELLED, pendingCustomerBooking.getStatus());

        confirmedCustomerBooking.cancelBooking();
        assertEquals(BookingStatus.CANCELLED, confirmedCustomerBooking.getStatus());
    }

    @Test
    void cancelBooking_ThrowsException_IfAlreadyCancelledOrRejected() {
        pendingCustomerBooking.cancelBooking(); // Now it's CANCELLED
        assertThrows(IllegalStateException.class, () -> pendingCustomerBooking.cancelBooking());

        Booking rejectedBooking = new Booking(3, 10, 100, null, null, LocalDate.now(), List.of(1), 0, 0, 0, 0, defaultParking, 0, BookingStatus.REJECTED);
        assertThrows(IllegalStateException.class, () -> rejectedBooking.cancelBooking());
    }

    // ==========================================
    // 3. TEST SU VARI UPDATE + LOGICA STATUS
    // ==========================================

    @Test
    void updateExtras_Succeeds_AndMaintainsPendingStatus() {
        pendingCustomerBooking.updateExtraSdraio(5);
        assertEquals(5, pendingCustomerBooking.getExtraSdraio());
        assertEquals(BookingStatus.PENDING, pendingCustomerBooking.getStatus());
    }

    @Test
    void updateExtras_RevertsToPending_IfConfirmed() {
        confirmedCustomerBooking.updateExtraLettini(2);

        assertEquals(2, confirmedCustomerBooking.getExtraLettini());
        assertEquals(BookingStatus.PENDING, confirmedCustomerBooking.getStatus(), "status = PENDING dopo una modifica");
    }

    @Test
    void updateSpotsAndParking_RevertsToPending_IfConfirmed() {
        BookingParking newParking = new BookingParking(2, 0, 0);
        List<Integer> newSpots = List.of(10, 11);

        confirmedCustomerBooking.updateSpotsAndParking(newSpots, newParking);

        assertEquals(2, confirmedCustomerBooking.getSpotIds().size());
        assertEquals(2, confirmedCustomerBooking.getParking().autoPark());
        assertEquals(BookingStatus.PENDING, confirmedCustomerBooking.getStatus());
    }

    @Test
    void updateMethods_ThrowException_IfCancelledOrRejected() {
        pendingCustomerBooking.cancelBooking();

        assertThrows(IllegalStateException.class, () -> pendingCustomerBooking.updateExtraSdraio(5));
        assertThrows(IllegalStateException.class, () -> pendingCustomerBooking.updateCamerini(1));
        assertThrows(IllegalStateException.class, () -> pendingCustomerBooking.updateSpotsAndParking(List.of(1), defaultParking));
    }

    @Test
    void updateTotalPrice_Succeeds_AndThrowsIfNegative() {
        pendingCustomerBooking.updateTotalPrice(150.0);
        assertEquals(150.0, pendingCustomerBooking.getTotalPrice());

        assertThrows(IllegalArgumentException.class, () -> pendingCustomerBooking.updateTotalPrice(-10.0));
    }

    @Test
    void updateSpotsAndParking_ThrowsException_IfParkingNegative() {
        //doppia sicurezza, anche se BookingParking blocca già i negativi
        assertThrows(IllegalArgumentException.class, () ->
                pendingCustomerBooking.updateSpotsAndParking(List.of(1), new BookingParking(-1, 0, 0))
        );
    }
}