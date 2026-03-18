package com.example.s_balneare.application.service.booking;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.AvailabilityQuery;
import com.example.s_balneare.application.port.out.booking.BookedInventory;
import com.example.s_balneare.application.port.out.booking.BookedParkingSpaces;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.service.TestTransactionManager;
import com.example.s_balneare.domain.beach.*;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingParking;
import com.example.s_balneare.domain.booking.BookingStatus;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.layout.Spot;
import com.example.s_balneare.domain.layout.SpotType;
import com.example.s_balneare.domain.layout.Zone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    //mock delle porte di uscita (il database fittizio)
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BeachRepository beachRepository;
    @Mock
    private AvailabilityQuery availabilityQuery;

    //System Under Test (SUT)
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        //uso la utility per bypassare SQL
        TransactionManager transactionManager = new TestTransactionManager();
        bookingService = new BookingService(
                bookingRepository, transactionManager, beachRepository, availabilityQuery
        );
    }

    // ==========================================
    // TEST UPDATE BOOKING
    // ==========================================

    @Test
    void updateBooking_Succeeds_WhenAllChecksPass() {
        //creo Beach e Booking
        LocalDate futureDate = LocalDate.now().plusDays(5);
        Booking mockBooking = createValidBooking(futureDate);
        Beach mockBeach = createValidBeach();
        BookingParking newParking = new BookingParking(1, 0, 0);

        //simulo il find
        when(bookingRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBooking));
        when(beachRepository.findById(eq(10), any())).thenReturn(Optional.of(mockBeach));

        //simulo che nel DB non ci siano parcheggi od oggetti prenotati
        when(availabilityQuery.getBookedParking(eq(10), eq(futureDate), eq(1), any()))
                .thenReturn(new BookedParkingSpaces(0, 0, 0));
        when(availabilityQuery.getBookedInventory(eq(10), eq(futureDate), eq(1), any()))
                .thenReturn(new BookedInventory(0, 0, 0, 0));

        //simulo che i nuovi spot appartengano effettivamente alla spiaggia
        when(beachRepository.doSpotsBelongToBeach(eq(10), anyList(), any())).thenReturn(true);

        //per il PriceCalculator, ho bisogno di mockare il metodo statico
        //usando try-with-resources per mockare un metodo statico con Mockito
        try (MockedStatic<com.example.s_balneare.domain.booking.PriceCalculator> mockedCalc = mockStatic(com.example.s_balneare.domain.booking.PriceCalculator.class)) {
            mockedCalc.when(() -> com.example.s_balneare.domain.booking.PriceCalculator.calculateTotal(any(), any())).thenReturn(100.0);

            //aggiungo nuovi extra al booking
            bookingService.updateBooking(1, List.of(1), newParking, 1, 1, 1, 1);

            //inserisco (non deve lanciare eccezioni)
            ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
            verify(bookingRepository).update(captor.capture(), any());

            //controllo che i dati siano stati aggiornati correttamente
            Booking savedBooking = captor.getValue();
            assertEquals(1, savedBooking.getExtraSdraio());
            assertEquals(1, savedBooking.getParking().autoPark());
            assertEquals(100.0, savedBooking.getTotalPrice());
            assertEquals(BookingStatus.PENDING, savedBooking.getStatus());
        }
    }

    @Test
    void updateBooking_ThrowsException_IfDateIsInThePastOrToday() {
        //creo nuovo Booking e Beach
        Booking mockBooking = createValidBooking(LocalDate.now());
        when(bookingRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBooking));
        when(beachRepository.findById(eq(10), any())).thenReturn(Optional.of(createValidBeach()));

        //mi assicuro che booking non venga aggiornato con la data odierna (lancia eccezione)
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                bookingService.updateBooking(1, List.of(1), BookingParking.empty(), 0, 0, 0, 0)
        );
        assertTrue(ex.getMessage().contains("cannot be updated on or after its date"));
        verify(bookingRepository, never()).update(any(), any());
    }

    @Test
    void updateBooking_ThrowsException_IfParkingNotAvailable() {
        //creo nuovo Booking e Beach
        LocalDate futureDate = LocalDate.now().plusDays(5);
        Booking mockBooking = createValidBooking(futureDate);
        //Beach ha 10 posti auto
        Beach mockBeach = createValidBeach();
        when(bookingRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBooking));
        when(beachRepository.findById(eq(10), any())).thenReturn(Optional.of(mockBeach));

        //simulo che nel DB ci siano già 10 auto prenotate
        when(availabilityQuery.getBookedParking(eq(10), eq(futureDate), eq(1), any()))
                .thenReturn(new BookedParkingSpaces(10, 0, 0));
        when(availabilityQuery.getBookedInventory(any(), any(), any(), any())).thenReturn(new BookedInventory(0, 0, 0, 0));

        //l'utente chiede 1 auto extra, ma la capienza totale è 10 e ce ne sono già 10 prenotate
        BookingParking newParking = new BookingParking(1, 0, 0);

        //deve lanciare eccezione
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                bookingService.updateBooking(1, List.of(1), newParking, 0, 0, 0, 0)
        );
        assertTrue(ex.getMessage().contains("too many parking spaces"));
    }

    @Test
    void updateBooking_ThrowsException_IfInventoryNotAvailable() {
        //creo nuovo Booking e Beach
        LocalDate futureDate = LocalDate.now().plusDays(5);
        Booking booking = createValidBooking(futureDate);
        //Beach ha 10 sdraio
        Beach beach = createValidBeach();
        when(bookingRepository.findById(eq(1), any())).thenReturn(Optional.of(booking));
        when(beachRepository.findById(eq(10), any())).thenReturn(Optional.of(beach));
        when(availabilityQuery.getBookedParking(any(), any(), any(), any())).thenReturn(new BookedParkingSpaces(0, 0, 0));

        //simulo che nel DB ci siano 6 sdraio già prenotate
        when(availabilityQuery.getBookedInventory(eq(10), eq(futureDate), eq(1), any()))
                .thenReturn(new BookedInventory(6, 0, 0, 0));

        //l'utente chiede 7 sdraio (6+7 = 13, ma il max è 10): deve lanciare eccezione
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                bookingService.updateBooking(1, List.of(1), BookingParking.empty(), 7, 0, 0, 0)
        );
        assertTrue(ex.getMessage().contains("not enough inventory items available"));
    }

    @Test
    void updateBooking_ThrowsException_IfSpotsDoNotBelongToBeach() {
        //creo nuovo Booking e Beach
        LocalDate futureDate = LocalDate.now().plusDays(5);
        Booking booking = createValidBooking(futureDate);
        when(bookingRepository.findById(eq(1), any())).thenReturn(Optional.of(booking));
        when(beachRepository.findById(eq(10), any())).thenReturn(Optional.of(createValidBeach()));
        when(availabilityQuery.getBookedParking(any(), any(), any(), any())).thenReturn(new BookedParkingSpaces(0, 0, 0));
        when(availabilityQuery.getBookedInventory(any(), any(), any(), any())).thenReturn(new BookedInventory(0, 0, 0, 0));

        //gli spot inviati appartengono a un'altra spiaggia
        when(beachRepository.doSpotsBelongToBeach(eq(10), anyList(), any())).thenReturn(false);

        //deve lanciare eccezione
        SecurityException ex = assertThrows(SecurityException.class, () ->
                bookingService.updateBooking(1, List.of(999), BookingParking.empty(), 0, 0, 0, 0)
        );
        assertTrue(ex.getMessage().contains("do not belong to the beach"));
    }

    // ==========================================
    // TEST CAMBIO STATO
    // ==========================================

    @Test
    void confirmBooking_Succeeds_IfFutureDate() {
        //creo nuovo Booking con data futura
        Booking booking = createValidBooking(LocalDate.now().plusDays(1));
        when(bookingRepository.findById(eq(1), any())).thenReturn(Optional.of(booking));

        //confermo la prenotazione
        bookingService.confirmBooking(1);

        //non deve lanciare eccezione
        verify(bookingRepository).update(eq(booking), any(TransactionContext.class));
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    }

    @Test
    void confirmBooking_ThrowsException_IfDateIsTodayOrPast() {
        //creo nuovo Booking con data passata
        Booking booking = createValidBooking(LocalDate.now().minusDays(4));
        when(bookingRepository.findById(eq(1), any())).thenReturn(Optional.of(booking));

        //deve lanciare eccezione
        assertThrows(IllegalStateException.class, () -> bookingService.confirmBooking(1));
        verify(bookingRepository, never()).update(any(), any());
    }

    @Test
    void rejectBooking_Succeeds_IfFutureDate() {
        //creo nuovo Booking con data futura
        Booking booking = createValidBooking(LocalDate.now().plusDays(1));
        when(bookingRepository.findById(eq(1), any())).thenReturn(Optional.of(booking));

        //rifiuto la prenotazione
        bookingService.rejectBooking(1);

        //non deve lanciare eccezione
        verify(bookingRepository).update(eq(booking), any(TransactionContext.class));
        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }

    @Test
    void rejectBooking_ThrowsException_IfDateIsTodayOrPast() {
        //creo nuovo Booking con data passata
        Booking booking = createValidBooking(LocalDate.now().minusDays(4));
        when(bookingRepository.findById(eq(1), any())).thenReturn(Optional.of(booking));

        //deve lanciare eccezione
        assertThrows(IllegalStateException.class, () -> bookingService.rejectBooking(1));
        verify(bookingRepository, never()).update(any(), any());
    }

    @Test
    void cancelBooking_Succeeds_IfFutureDate() {
        //creo nuovo Booking con data futura
        Booking booking = createValidBooking(LocalDate.now().plusDays(1));
        when(bookingRepository.findById(eq(1), any())).thenReturn(Optional.of(booking));

        //cancello la prenotazione
        bookingService.cancelBooking(1);

        //non deve lanciare eccezione
        verify(bookingRepository).update(eq(booking), any(TransactionContext.class));
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    void cancelBooking_ThrowsException_IfDateIsTodayOrPast() {
        //creo nuovo Booking con data odierna
        Booking booking = createValidBooking(LocalDate.now());
        when(bookingRepository.findById(eq(1), any())).thenReturn(Optional.of(booking));

        //deve lanciare eccezione
        assertThrows(IllegalStateException.class, () -> bookingService.cancelBooking(1));
        verify(bookingRepository, never()).update(any(), any());
    }

    // ==========================================
    // TEST QUERY METODI LISTE
    // ==========================================

    @Test
    void getCustomerBookings_CallsRepository() {
        //creo nuovo Booking con data odierna
        List<Booking> expected = List.of(createValidBooking(LocalDate.now()));
        when(bookingRepository.findByCustomerId(eq(100), any())).thenReturn(expected);

        //chiamo il metodo
        List<Booking> result = bookingService.getCustomerBookings(100);

        //deve ritornare la lista di prenotazioni (1 oggetto)
        assertEquals(1, result.size());
        verify(bookingRepository).findByCustomerId(eq(100), any());
    }

    @Test
    void getBeachBookings_ThrowsException_IfOwnerHasNoBeach() {
        //mocka il comportamento del metodo findByOwnerId (non restituisce nulla)
        when(beachRepository.findByOwnerId(eq(999), any())).thenReturn(Optional.empty());

        //deve lanciare eccezione
        assertThrows(IllegalArgumentException.class, () -> bookingService.getBeachBookings(999));
        verify(bookingRepository, never()).findByBeachId(any(), any());
    }

    @Test
    void getBeachBookings_Succeeds() {
        //creo nuova Beach e Booking
        Beach beach = createValidBeach();
        when(beachRepository.findByOwnerId(eq(500), any())).thenReturn(Optional.of(beach));
        List<Booking> expected = List.of(createValidBooking(LocalDate.now()));
        when(bookingRepository.findByBeachId(eq(10), any())).thenReturn(expected);

        //chiamo il metodo
        List<Booking> result = bookingService.getBeachBookings(500);

        //deve ritornare la lista di prenotazioni (1 oggetto)
        assertEquals(1, result.size());
        verify(bookingRepository).findByBeachId(eq(10), any());
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private Booking createValidBooking(LocalDate date) {
        return new Booking(1, 10, 100, null, null, date, List.of(1), 0, 0, 0, 0, BookingParking.empty(), 0.0, BookingStatus.PENDING);
    }

    private Beach createValidBeach() {
        return new Beach(10, 500, 1,
                new BeachGeneral("Lido", "Desc", "+3900"),
                new BeachInventory(10, 10, 10, 10),
                BeachServices.none(),
                new Parking(10, 10, 10, false),
                "Extra", List.of(), List.of(Zone.create("A").withSpots(List.of(Spot.create(SpotType.UMBRELLA, 1, 1)))), false, false);
    }
}