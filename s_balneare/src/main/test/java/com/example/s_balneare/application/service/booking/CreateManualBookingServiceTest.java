package com.example.s_balneare.application.service.booking;

import com.example.s_balneare.application.port.in.booking.CreateManualBookingCommand;
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
class CreateManualBookingServiceTest {
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
    private CreateManualBookingService createManualBookingService;

    @BeforeEach
    void setUp() {
        //uso la utility per bypassare SQL
        TransactionManager transactionManager = new TestTransactionManager();
        createManualBookingService = new CreateManualBookingService(
                beachRepository, bookingRepository, availabilityQuery, banRepository, transactionManager
        );
    }

    // ==========================================
    // TEST DEL PERCORSO DI SUCCESSO
    // ==========================================

    @Test
    void createManualBooking_Succeeds_WhenAllChecksPass() {
        //creo nuova Beach
        CreateManualBookingCommand command = createValidCommand();
        Beach activeBeach = createValidBeach(true);
        when(beachRepository.findByOwnerId(eq(command.ownerId()), any())).thenReturn(Optional.of(activeBeach));
        //owner non bannato dall'app
        when(banRepository.isBannedFromApp(eq(command.ownerId()), any())).thenReturn(false);
        when(availabilityQuery.getBookedParking(eq(activeBeach.getId()), eq(command.date()), isNull(), any()))
                .thenReturn(new BookedParkingSpaces(0, 0, 0));
        when(availabilityQuery.getBookedInventory(eq(activeBeach.getId()), eq(command.date()), isNull(), any()))
                .thenReturn(new BookedInventory(0, 0, 0, 0));
        //simulo che gli spot appartengano alla spiaggia
        when(beachRepository.doSpotsBelongToBeach(eq(activeBeach.getId()), eq(command.spotIds()), any())).thenReturn(true);
        //simulo l'ID restituito dal salvataggio
        when(bookingRepository.save(any(Booking.class), any())).thenReturn(55);

        //per il PriceCalculator, ho bisogno di mockare il metodo statico
        //usando try-with-resources per mockare un metodo statico con Mockito
        try (MockedStatic<PriceCalculator> mockedPriceCalc = mockStatic(PriceCalculator.class)) {
            mockedPriceCalc.when(() -> PriceCalculator.calculateTotal(any(), any())).thenReturn(75.50);

            //creo il Booking
            Integer bookingId = createManualBookingService.createManualBooking(command);

            //controllo che mi sia stato restituito l'ID della prenotazione generato dal DB
            assertEquals(55, bookingId);

            //controllo che la prenotazione creata abbia i valori giusti
            ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
            verify(bookingRepository).save(captor.capture(), any());

            Booking savedBooking = captor.getValue();
            assertEquals(activeBeach.getId(), savedBooking.getBeachId());
            assertNull(savedBooking.getCustomerId());
            assertEquals("Mario Rossi", savedBooking.getCallerName());
            assertEquals("+393331234567", savedBooking.getCallerPhone());
            assertEquals(BookingStatus.CONFIRMED, savedBooking.getStatus());
            assertEquals(75.50, savedBooking.getTotalPrice());
        }
    }

    // ==========================================
    // TEST FALLIMENTI: OWNER E SPIAGGIA
    // ==========================================

    @Test
    void createManualBooking_ThrowsException_IfOwnerHasNoBeach() {
        //creo comando valido per creazione prenotazione, ma non ho Beach salvata nel DB
        CreateManualBookingCommand command = createValidCommand();
        when(beachRepository.findByOwnerId(eq(command.ownerId()), any())).thenReturn(Optional.empty());

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createManualBookingService.createManualBooking(command));
        assertTrue(ex.getMessage().contains("does not have a registered beach"));
        verify(bookingRepository, never()).save(any(), any());
    }

    @Test
    void createManualBooking_ThrowsException_IfBeachIsInactive() {
        //creo comando valido per creazione prenotazione, ma Beach non è attiva
        CreateManualBookingCommand command = createValidCommand();
        Beach inactiveBeach = createValidBeach(false);
        when(beachRepository.findByOwnerId(eq(command.ownerId()), any())).thenReturn(Optional.of(inactiveBeach));

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createManualBookingService.createManualBooking(command));
        assertTrue(ex.getMessage().contains("inactive beach"));
        verify(bookingRepository, never()).save(any(), any());
    }

    @Test
    void createManualBooking_ThrowsException_IfOwnerBannedFromApp() {
        //creo comando valido per creazione prenotazione, ma Owner è bannato
        CreateManualBookingCommand command = createValidCommand();
        Beach activeBeach = createValidBeach(true);
        when(beachRepository.findByOwnerId(eq(command.ownerId()), any())).thenReturn(Optional.of(activeBeach));
        when(banRepository.isBannedFromApp(eq(command.ownerId()), any())).thenReturn(true);

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createManualBookingService.createManualBooking(command));
        assertTrue(ex.getMessage().contains("owner banned from the app"));
        verify(bookingRepository, never()).save(any(), any());
    }

    // ==========================================
    // TEST FALLIMENTI: DISPONIBILITÀ E SICUREZZA
    // ==========================================

    @Test
    void createManualBooking_ThrowsException_IfParkingCapacityExceeded() {
        //creo comando che richiede 1 auto
        CreateManualBookingCommand command = createValidCommand();
        Beach activeBeach = createValidBeach(true); //capienza totale: 10 auto
        when(beachRepository.findByOwnerId(eq(command.ownerId()), any())).thenReturn(Optional.of(activeBeach));
        when(banRepository.isBannedFromApp(any(), any())).thenReturn(false);

        //simulo che i 10 posti auto siano già tutti occupati per quel giorno
        when(availabilityQuery.getBookedParking(any(), any(), isNull(), any()))
                .thenReturn(new BookedParkingSpaces(10, 0, 0));

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createManualBookingService.createManualBooking(command));
        assertTrue(ex.getMessage().contains("not enough parking capacity"));
        verify(bookingRepository, never()).save(any(), any());
    }

    @Test
    void createManualBooking_ThrowsException_IfInventoryCapacityExceeded() {
        //creo comando che richiede 1 lettino
        CreateManualBookingCommand command = createValidCommand();
        Beach activeBeach = createValidBeach(true); //inventario: 10 lettini
        when(beachRepository.findByOwnerId(eq(command.ownerId()), any())).thenReturn(Optional.of(activeBeach));
        when(banRepository.isBannedFromApp(any(), any())).thenReturn(false);
        when(availabilityQuery.getBookedParking(any(), any(), isNull(), any())).thenReturn(new BookedParkingSpaces(0, 0, 0));

        //simulo che i 10 lettini siano già tutti prenotati
        when(availabilityQuery.getBookedInventory(any(), any(), isNull(), any()))
                .thenReturn(new BookedInventory(0, 10, 0, 0));

        //deve lanciare eccezione e non salvare nulla
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> createManualBookingService.createManualBooking(command));
        assertTrue(ex.getMessage().contains("not enough inventory"));
        verify(bookingRepository, never()).save(any(), any());
    }

    @Test
    void createManualBooking_ThrowsException_IfSpotsDoNotBelongToBeach() {
        //creo comando che valido per creazione prenotazione, ma...
        CreateManualBookingCommand command = createValidCommand();
        Beach activeBeach = createValidBeach(true);
        when(beachRepository.findByOwnerId(eq(command.ownerId()), any())).thenReturn(Optional.of(activeBeach));
        when(banRepository.isBannedFromApp(any(), any())).thenReturn(false);
        when(availabilityQuery.getBookedParking(any(), any(), isNull(), any())).thenReturn(new BookedParkingSpaces(0, 0, 0));
        when(availabilityQuery.getBookedInventory(any(), any(), isNull(), any())).thenReturn(new BookedInventory(0, 0, 0, 0));

        //...lo spot 1 non appartiene a questa spiaggia
        when(beachRepository.doSpotsBelongToBeach(eq(activeBeach.getId()), eq(command.spotIds()), any())).thenReturn(false);

        //deve lanciare eccezione e non salvare nulla
        SecurityException ex = assertThrows(SecurityException.class, () -> createManualBookingService.createManualBooking(command));
        assertTrue(ex.getMessage().contains("do not belong to the beach"));
        verify(bookingRepository, never()).save(any(), any());
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private CreateManualBookingCommand createValidCommand() {
        return new CreateManualBookingCommand(
                500,
                "Mario Rossi", "+393331234567",
                LocalDate.now().plusDays(10), List.of(1),
                1, 0, 0,
                0, 1, 0, 0
        );
    }

    private Beach createValidBeach(boolean isActive) {
        return new Beach(10, 500, 1,
                new BeachGeneral("Lido Owner", "Desc", "+3900"),
                //capacità inventario: 10 per ciascun oggetto
                new BeachInventory(10, 10, 10, 10),
                BeachServices.none(),
                //capacità parcheggio: 10 per ciascun tipo di parcheggio
                new Parking(10, 10, 10, false),
                "Extra", List.of(), List.of(Zone.create("A")), isActive, false);
    }
}