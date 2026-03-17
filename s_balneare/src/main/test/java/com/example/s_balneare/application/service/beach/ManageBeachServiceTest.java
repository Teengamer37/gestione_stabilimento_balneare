package com.example.s_balneare.application.service.beach;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.booking.BookedInventory;
import com.example.s_balneare.application.port.out.booking.BookedParkingSpaces;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.service.TestTransactionManager;
import com.example.s_balneare.domain.beach.*;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.layout.Spot;
import com.example.s_balneare.domain.layout.SpotType;
import com.example.s_balneare.domain.layout.Zone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManageBeachServiceTest {
    //mock delle porte di uscita (il database fittizio)
    @Mock
    private BeachRepository beachRepository;
    @Mock
    private BookingRepository bookingRepository;

    //System Under Test (SUT)
    private ManageBeachService manageBeachService;

    @BeforeEach
    void setUp() {
        //uso la utility per bypassare SQL
        TransactionManager transactionManager = new TestTransactionManager();
        manageBeachService = new ManageBeachService(beachRepository, bookingRepository, transactionManager);
    }

    // ==========================================
    // TEST METODI UPDATE SEMPLICI
    // ==========================================

    @Test
    void updateGeneralInfo_Succeeds() {
        //creo nuova Beach
        Beach mockBeach = createValidInactiveBeach();
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBeach));

        //creo nuovo BeachGeneral e faccio update
        BeachGeneral newGeneral = new BeachGeneral("Nuovo Lido", "Desc", "+39000000");
        manageBeachService.updateGeneralInfo(1, newGeneral);

        //mi assicuro abbia fatto update su beach
        verify(beachRepository).update(eq(mockBeach), any(TransactionContext.class));
        assertEquals("Nuovo Lido", mockBeach.getBeachGeneral().name());
    }

    @Test
    void updateServices_Succeeds() {
        //creo nuova Beach
        Beach mockBeach = createValidInactiveBeach();
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBeach));

        //creo nuovo BeachServices e faccio update
        BeachServices newServices = BeachServices.builder().wifi(true).build();
        manageBeachService.updateServices(1, newServices);

        //mi assicuro abbia fatto update su beach
        verify(beachRepository).update(eq(mockBeach), any(TransactionContext.class));
        assertTrue(mockBeach.getBeachServices().wifi());
    }

    @Test
    void updateExtraInfo_Succeeds() {
        //creo nuova Beach
        Beach mockBeach = createValidInactiveBeach();
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBeach));

        //faccio update
        manageBeachService.updateExtraInfo(1, "Amiamo tutti Marco");

        //verifico che le info extra siano state aggiornate
        verify(beachRepository).update(eq(mockBeach), any(TransactionContext.class));
        assertEquals("Amiamo tutti Marco", mockBeach.getExtraInfo());
    }

    // ==========================================
    // TEST CONTROLLI CAPIENZA
    // ==========================================

    @Test
    void updateInventory_Succeeds_IfCapacityIsSufficient() {
        //creo nuova Beach
        Beach mockBeach = createValidInactiveBeach();
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBeach));

        //simulo che nel futuro ci siano al massimo 2 sdraio prenotate
        when(bookingRepository.getMaxFutureInventory(eq(1), any(), any()))
                .thenReturn(new BookedInventory(2, 0, 0, 0));

        //aggiorno inventario
        BeachInventory newInv = new BeachInventory(10, 10, 10, 10);
        assertDoesNotThrow(() -> manageBeachService.updateInventory(1, newInv));

        //verifico update
        verify(beachRepository).update(eq(mockBeach), any(TransactionContext.class));
    }

    @Test
    void updateInventory_ThrowsException_IfCapacityBreaksFutureBookings() {
        //creo nuova Beach
        Beach mockBeach = createValidInactiveBeach();
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBeach));

        //simulo che ci siano 15 sdraio extra già prenotati nel futuro
        when(bookingRepository.getMaxFutureInventory(eq(1), any(), any()))
                .thenReturn(new BookedInventory(15, 0, 0, 0));

        //owner tenta di ridurne il numero a 10
        BeachInventory newInv = new BeachInventory(10, 10, 10, 10);

        //deve lanciare eccezione
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                manageBeachService.updateInventory(1, newInv));
        assertTrue(ex.getMessage().contains("does not satisfy all future bookings"));
    }

    @Test
    void updateParking_Succeeds_IfCapacityIsSufficient() {
        //creo nuova Beach
        Beach mockBeach = createValidInactiveBeach();
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBeach));

        //simulo che ci siano prenotati 5 parcheggi di ogni tipo
        when(bookingRepository.getMaxFutureParkings(eq(1), any(), any()))
                .thenReturn(new BookedParkingSpaces(5, 5, 5));

        //aggiorno numeri parcheggio
        Parking newParking = new Parking(10, 10, 10, false);
        assertDoesNotThrow(() -> manageBeachService.updateParking(1, newParking));

        //non deve lanciare eccezione
        verify(beachRepository).update(eq(mockBeach), any(TransactionContext.class));
    }

    @Test
    void updateParking_ThrowsException_IfCapacityBreaksFutureBookings() {
        //creo nuova Beach
        Beach mockBeach = createValidInactiveBeach();
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBeach));

        //simulo che ci siano 20 parcheggi auto già prenotati nel futuro
        when(bookingRepository.getMaxFutureParkings(eq(1), any(), any()))
                .thenReturn(new BookedParkingSpaces(20, 0, 0));

        //owner tenta di abbassare la capienza massima a 10
        Parking newParking = new Parking(10, 0, 0, false);

        //deve lanciare eccezione
        assertThrows(IllegalStateException.class, () -> manageBeachService.updateParking(1, newParking));
    }

    // ==========================================
    // TEST STATO ATTIVITÀ
    // ==========================================

    @Test
    void setBeachActive_True_UpdatesStatus_WhenFullyConfigured() {
        //uso una spiaggia completamente configurata per passare la validazione del Dominio
        Beach mockBeach = createFullyConfiguredBeach();
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBeach));

        manageBeachService.setBeachActive(1, true);

        //non deve lanciare eccezioni
        verify(beachRepository).updateStatus(eq(1), eq(true), any());
        assertTrue(mockBeach.isActive());
    }

    @Test
    void setBeachActive_True_NotUpdatesStatus_WhenNotFullyConfigured() {
        //uso una spiaggia non completamente configurata
        Beach mockBeach = new Beach(1, 100, 200, new BeachGeneral("a", "b", "+123242"), null, null, null, "", null, null, false, false);
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBeach));

        //deve lanciare eccezione
        assertThrows(IllegalStateException.class, () -> manageBeachService.setBeachActive(1, true));

        //mi assicuro rimanga active = FALSE
        assertFalse(mockBeach.isActive());
    }

    @Test
    void setBeachActive_False_BypassesDomainValidation_AndUsesFastPath() {
        //per la disattivazione basta che la spiaggia esista, non serve caricarla interamente
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mock(Beach.class)));

        manageBeachService.setBeachActive(1, false);

        //verifico il "fast path" SQL
        verify(beachRepository).updateStatus(eq(1), eq(false), any());
    }

    // ==========================================
    // TEST SEASONS E ZONES
    // ==========================================

    @Test
    void addSeason_Succeeds() {
        //creo nuova Beach
        Beach mockBeach = createValidInactiveBeach();
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBeach));

        //creo e aggiongo stagione
        Season newSeason = createSeason("Autunno", LocalDate.now().plusMonths(3));
        manageBeachService.addSeason(1, newSeason);

        //non dovrebbe lanciare eccezione
        verify(beachRepository).update(eq(mockBeach), any(TransactionContext.class));
        assertTrue(mockBeach.getSeasons().stream().anyMatch(s -> s.name().equals("Autunno")));
    }

    @Test
    void updateSeasonEndDate_Succeeds() {
        //creo nuova Beach
        Beach mockBeach = createValidInactiveBeach();
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBeach));

        //estendo la data di fine
        LocalDate extendedDate = mockBeach.getSeasons().getFirst().endDate().plusDays(10);
        manageBeachService.updateSeasonEndDate(1, "Summer", extendedDate);

        //non deve lanciare eccezione
        verify(beachRepository).update(eq(mockBeach), any(TransactionContext.class));
        assertEquals(extendedDate, mockBeach.getSeasons().getFirst().endDate());
    }

    @Test
    void updateSeasonEndDate_ThrowsException() {
        //creo nuova Beach
        Beach mockBeach = createValidInactiveBeach();
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBeach));

        //accorcio la data di fine
        LocalDate shortenedDate = mockBeach.getSeasons().getFirst().endDate().minusDays(10);

        //deve lanciare eccezione
        assertThrows(IllegalArgumentException.class, () -> manageBeachService.updateSeasonEndDate(1, "Summer", shortenedDate));
    }

    @Test
    void removeSeason_Succeeds_IfNoBookingsExist() {
        //creo nuova Beach
        Beach mockBeach = createValidInactiveBeach();
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBeach));

        //simulo che non ci siano prenotazioni per quella stagione
        when(bookingRepository.hasBookingsForSeason(eq(1), any(), any(), any())).thenReturn(false);
        manageBeachService.removeSeason(1, "Summer");

        //non deve lanciare eccezioni
        verify(beachRepository).update(eq(mockBeach), any(TransactionContext.class));
        assertTrue(mockBeach.getSeasons().isEmpty());
    }

    @Test
    void removeSeason_ThrowsException_IfBookingsExist() {
        //creo nuova Beach
        Beach mockBeach = createValidInactiveBeach();
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBeach));

        //simulo che la stagione abbia prenotazioni attive/passate
        when(bookingRepository.hasBookingsForSeason(eq(1), any(), any(), any())).thenReturn(true);

        //deve lanciare eccezioni
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                manageBeachService.removeSeason(1, "Summer"));
        assertTrue(ex.getMessage().contains("associated one or more bookings"));
    }

    @Test
    void zoneOperations_AddAndRename_Succeed() {
        //creo nuova Beach
        Beach mockBeach = createValidInactiveBeach();
        when(beachRepository.findById(eq(1), any())).thenReturn(Optional.of(mockBeach));

        //test addZone()
        Zone newZone = Zone.create("Zona 2");
        manageBeachService.addZone(1, newZone);
        verify(beachRepository, times(1)).update(eq(mockBeach), any(TransactionContext.class));

        //test renameZone()
        manageBeachService.renameZone(1, "Zona 2", "Zona VIP");
        verify(beachRepository, times(1)).renameZone(eq(1), eq("Zona 2"), eq("Zona VIP"), any());
    }

    // ==========================================
    // TEST ECCEZIONI DI RECUPERO
    // ==========================================

    @Test
    void getBeachOrThrow_ThrowsException_IfNotFound() {
        when(beachRepository.findById(eq(99), any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> manageBeachService.updateExtraInfo(99, "Test"));
        assertThrows(IllegalArgumentException.class, () -> manageBeachService.getBeach(99));
    }

    // ==========================================
    // HELPER METHODS PER CREAZIONE SPIAGGE
    // ==========================================

    private Beach createValidInactiveBeach() {
        // Una spiaggia con 1 stagione e 1 zona (libera), pronta per le modifiche
        Season summer = createSeason("Summer", LocalDate.now().plusDays(1));
        Zone zone1 = Zone.create("Zone 1").withSpots(List.of(Spot.create(SpotType.UMBRELLA, 1, 1)));

        return new Beach(1, 100, 200,
                new BeachGeneral("Lido", "Desc", "+3900"),
                BeachInventory.empty(), BeachServices.none(), Parking.empty(),
                "", List.of(summer), List.of(zone1), false, false);
    }

    private Beach createFullyConfiguredBeach() {
        // Una spiaggia con tutti i requisiti per essere attivata
        Season summer = createSeason("Summer", LocalDate.now().plusDays(1));
        Zone zone1 = Zone.create("Zone 1").withSpots(List.of(Spot.create(SpotType.UMBRELLA, 1, 1)));

        return new Beach(1, 100, 200,
                new BeachGeneral("Lido", "Desc", "+3900"),
                new BeachInventory(10,10,10,10),
                BeachServices.builder().wifi(true).build(),
                new Parking(10,10,10,false),
                "Extra", List.of(summer), List.of(zone1), false, false);
    }

    private Season createSeason(String name, LocalDate start) {
        return Season.builder()
                .name(name)
                .startDate(start)
                .endDate(start.plusMonths(1))
                .pricing(Pricing.create(10, 10, 10, 10, 10))
                .zoneTariffs(List.of(ZoneTariff.create("Zone 1", 10, 10)))
                .build();
    }
}