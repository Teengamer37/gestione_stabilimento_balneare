package com.example.s_balneare.application.service.booking;

import com.example.s_balneare.application.port.in.booking.CreateBookingCommand;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.AvailabilityQuery;
import com.example.s_balneare.application.port.out.booking.BookedInventory;
import com.example.s_balneare.application.port.out.booking.BookedParkingSpaces;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.moderation.BanRepository;
import com.example.s_balneare.application.service.TestTransactionManager;
import com.example.s_balneare.domain.beach.*;
import com.example.s_balneare.domain.booking.Booking;
import com.example.s_balneare.domain.booking.BookingStatus;
import com.example.s_balneare.domain.booking.PriceCalculator;
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
class CreateBookingServiceTest {
    //mock delle porte di uscita (il database fittizio)
    @Mock
    private BeachRepository beachRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private AvailabilityQuery availabilityQuery;
    @Mock
    private BanRepository banRepository;

    //System Under Test (SUT)
    private CreateBookingService createBookingService;

    @BeforeEach
    void setUp() {
        //uso la utility per bypassare SQL
        TransactionManager transactionManager = new TestTransactionManager();
        createBookingService = new CreateBookingService(
                beachRepository, bookingRepository, availabilityQuery, banRepository, transactionManager
        );
    }

    // ==========================================
    // TEST DEL PERCORSO DI SUCCESSO
    // ==========================================

    @Test
    void createBooking_Succeeds_WhenAllChecksPass() {
        //creo nuova Beach
        CreateBookingCommand command = createValidCommand();
        Beach mockBeach = createValidBeach(true);
        when(beachRepository.findById(eq(command.beachId()), any())).thenReturn(Optional.of(mockBeach));
        //utente non ha ban attivi
        when(banRepository.isBannedFromApp(eq(command.customerId()), any())).thenReturn(false);
        when(banRepository.isBannedFromBeach(eq(command.customerId()), eq(command.beachId()), any())).thenReturn(false);

        //simulo che nel DB ci siano 0 parcheggi e 0 oggetti extra occupati per quella data
        when(availabilityQuery.getBookedParking(eq(command.beachId()), eq(command.date()), isNull(), any()))
                .thenReturn(new BookedParkingSpaces(0, 0, 0));
        when(availabilityQuery.getBookedInventory(eq(command.beachId()), eq(command.date()), isNull(), any()))
                .thenReturn(new BookedInventory(0, 0, 0, 0));

        //simulo che gli spot appartengano alla spiaggia
        when(beachRepository.doSpotsBelongToBeach(eq(command.beachId()), eq(command.spotIds()), any())).thenReturn(true);

        //simulo l'ID restituito dal salvataggio
        when(bookingRepository.save(any(Booking.class), any())).thenReturn(99);

        //per il PriceCalculator, ho bisogno di mockare il metodo statico
        //usando try-with-resources per mockare un metodo statico con Mockito
        try (MockedStatic<PriceCalculator> mockedPriceCalc = mockStatic(PriceCalculator.class)) {
            mockedPriceCalc.when(() -> PriceCalculator.calculateTotal(any(), any())).thenReturn(150.0);

            //creo il Booking
            Integer bookingId = createBookingService.createBooking(command);

            //verifico mi sia stato restituito l'ID della prenotazione generato dal DB
            assertEquals(99, bookingId);

            //verifico che la prenotazione creata abbia i valori giusti
            ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
            verify(bookingRepository).save(captor.capture(), any());

            Booking savedBooking = captor.getValue();
            assertEquals(command.beachId(), savedBooking.getBeachId());
            assertEquals(command.customerId(), savedBooking.getCustomerId());
            assertEquals(BookingStatus.PENDING, savedBooking.getStatus());
            assertEquals(150.0, savedBooking.getTotalPrice());
            assertEquals(1, savedBooking.getExtraSdraio());
            assertEquals(1, savedBooking.getParking().autoPark());
        }
    }

    // ==========================================
    // TEST FALLIMENTI: SPIAGGIA E UTENTE
    // ==========================================

    @Test
    void createBooking_ThrowsException_IfBeachNotFound() {
        //creo nuovo comando per creazione prenotazione, ma non ho Beach salvata nel DB
        CreateBookingCommand command = createValidCommand();
        when(beachRepository.findById(eq(command.beachId()), any())).thenReturn(Optional.empty());

        //deve lanciare eccezione e non salvare nulla
        assertThrows(IllegalArgumentException.class, () -> createBookingService.createBooking(command));
        verify(bookingRepository, never()).save(any(), any());
    }

    @Test
    void createBooking_ThrowsException_IfBeachIsInactive() {
        //creo nuovo comando per creazione prenotazione, ma la Beach è disattivata
        CreateBookingCommand command = createValidCommand();
        Beach inactiveBeach = createValidBeach(false);
        when(beachRepository.findById(eq(command.beachId()), any())).thenReturn(Optional.of(inactiveBeach));

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createBookingService.createBooking(command));
        assertTrue(ex.getMessage().contains("inactive beach"));
        verify(bookingRepository, never()).save(any(), any());
    }

    @Test
    void createBooking_ThrowsException_IfCustomerBannedFromApp() {
        //creo nuovo comando per creazione prenotazione, ma il Customer è bannato dall'app
        CreateBookingCommand command = createValidCommand();
        Beach activeBeach = createValidBeach(true);
        when(beachRepository.findById(eq(command.beachId()), any())).thenReturn(Optional.of(activeBeach));
        when(banRepository.isBannedFromApp(eq(command.customerId()), any())).thenReturn(true);

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createBookingService.createBooking(command));
        assertTrue(ex.getMessage().contains("banned from the app"));
        verify(bookingRepository, never()).save(any(), any());
    }

    @Test
    void createBooking_ThrowsException_IfCustomerBannedFromBeach() {
        //creo nuovo comando per creazione prenotazione, ma il Customer è bannato dalla spiaggia
        CreateBookingCommand command = createValidCommand();
        Beach activeBeach = createValidBeach(true);
        when(beachRepository.findById(eq(command.beachId()), any())).thenReturn(Optional.of(activeBeach));
        when(banRepository.isBannedFromApp(eq(command.customerId()), any())).thenReturn(false);
        when(banRepository.isBannedFromBeach(eq(command.customerId()), eq(command.beachId()), any())).thenReturn(true);

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createBookingService.createBooking(command));
        assertTrue(ex.getMessage().contains("banned from the beach"));
        verify(bookingRepository, never()).save(any(), any());
    }

    // ==========================================
    // TEST FALLIMENTI: DISPONIBILITÀ CAPACITÀ
    // ==========================================

    @Test
    void createBooking_ThrowsException_IfParkingCapacityExceeded() {
        //creo comando che richiede 1 posto auto
        CreateBookingCommand command = createValidCommand();
        Beach activeBeach = createValidBeach(true); //capienza spiaggia: 10 auto
        when(beachRepository.findById(eq(command.beachId()), any())).thenReturn(Optional.of(activeBeach));
        when(banRepository.isBannedFromApp(any(), any())).thenReturn(false);
        when(banRepository.isBannedFromBeach(any(), any(), any())).thenReturn(false);

        //simulo che ci siano già 10 auto prenotate
        when(availabilityQuery.getBookedParking(eq(command.beachId()), eq(command.date()), isNull(), any()))
                .thenReturn(new BookedParkingSpaces(10, 0, 0));

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createBookingService.createBooking(command));
        assertTrue(ex.getMessage().contains("not enough parking capacity"));
        verify(bookingRepository, never()).save(any(), any());
    }

    @Test
    void createBooking_ThrowsException_IfInventoryCapacityExceeded() {
        //creo comando che richiede 1 sdraio in più
        CreateBookingCommand command = createValidCommand();
        Beach activeBeach = createValidBeach(true); //capienza spiaggia: 10 sdraio
        when(beachRepository.findById(eq(command.beachId()), any())).thenReturn(Optional.of(activeBeach));
        when(banRepository.isBannedFromApp(any(), any())).thenReturn(false);
        when(banRepository.isBannedFromBeach(any(), any(), any())).thenReturn(false);
        when(availabilityQuery.getBookedParking(any(), any(), any(), any())).thenReturn(new BookedParkingSpaces(0, 0, 0));

        //simulo che ci siano già 10 sdraio prenotate per quel giorno
        when(availabilityQuery.getBookedInventory(eq(command.beachId()), eq(command.date()), isNull(), any()))
                .thenReturn(new BookedInventory(10, 0, 0, 0));

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createBookingService.createBooking(command));
        assertTrue(ex.getMessage().contains("not enough inventory items available"));
        verify(bookingRepository, never()).save(any(), any());
    }

    // ==========================================
    // TEST FALLIMENTI: SICUREZZA LAYOUT
    // ==========================================

    @Test
    void createBooking_ThrowsException_IfSpotsDoNotBelongToBeach() {
        //creo comando valido per creazione prenotazione, ma...
        CreateBookingCommand command = createValidCommand();
        Beach activeBeach = createValidBeach(true);
        when(beachRepository.findById(eq(command.beachId()), any())).thenReturn(Optional.of(activeBeach));
        when(banRepository.isBannedFromApp(any(), any())).thenReturn(false);
        when(banRepository.isBannedFromBeach(any(), any(), any())).thenReturn(false);
        when(availabilityQuery.getBookedParking(any(), any(), any(), any())).thenReturn(new BookedParkingSpaces(0, 0, 0));
        when(availabilityQuery.getBookedInventory(any(), any(), any(), any())).thenReturn(new BookedInventory(0, 0, 0, 0));

        //...lo spot 1 non appartiene a questa spiaggia
        when(beachRepository.doSpotsBelongToBeach(eq(command.beachId()), eq(command.spotIds()), any())).thenReturn(false);

        //deve lanciare eccezione e non salvare nulla
        SecurityException ex = assertThrows(SecurityException.class, () -> createBookingService.createBooking(command));
        assertTrue(ex.getMessage().contains("spots do not belong to the beach"));
        verify(bookingRepository, never()).save(any(), any());
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private CreateBookingCommand createValidCommand() {
        return new CreateBookingCommand(
                10, 100, LocalDate.now().plusDays(10), List.of(1),
                1, 0, 0,
                1, 0, 0, 0
        );
    }

    private Beach createValidBeach(boolean isActive) {
        return new Beach(10, 1, 1,
                new BeachGeneral("Lido Test", "Desc", "+3900"),
                //capacità inventario: 10 per ciascun oggetto
                new BeachInventory(10, 10, 10, 10),
                BeachServices.none(),
                //capacità parcheggio: 10 per ciascun tipo di parcheggio
                new Parking(10, 10, 10, false),
                "Extra", List.of(), List.of(Zone.create("A")), isActive, false);
    }
}