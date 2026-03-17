package com.example.s_balneare.domain.beach;

import com.example.s_balneare.domain.layout.Zone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BeachTest {
    private Beach draftBeach;

    @BeforeEach
    void setUp() {
        //creazione spiaggia vuota, inizializzata solo con parametri obbligatori
        draftBeach = new Beach(
                1, 100, 200,
                createValidGeneral(), null, null, null,
                "", null, null, false, false
        );
    }

    // ==========================================
    // 1. TEST SUL COSTRITTORE
    // ==========================================

    @Test
    void constructor_ThrowsException_IfAddressIdNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Beach(1, 100, null, createValidGeneral(), null, null, null, "", null, null, false, false)
        );
        assertTrue(ex.getMessage().contains("addressId cannot be null"));
    }

    @Test
    void constructor_ThrowsException_IfBeachGeneralNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Beach(1, 100, 200, null, null, null, null, "", null, null, false, false)
        );
        assertTrue(ex.getMessage().contains("beachGeneral cannot be null"));
    }

    @Test
    void constructor_ExtractsZonesFromSeasons_Automatically() {
        Season season = createValidSeason("Estate", LocalDate.now().plusDays(1), LocalDate.now().plusMonths(1), "Zona Base");
        Beach beach = new Beach(1, 100, 200, createValidGeneral(), null, null, null, "", List.of(season), null, false, false);

        assertEquals(1, beach.getZones().size());
        assertEquals("Zona Base", beach.getZones().getFirst().name());
    }

    // ==========================================
    // 2. TEST SU ACTIVE E SU MODIFICHE
    // ==========================================

    @Test
    void isFullyConfigured_ReturnsFalse_WhenMissingData() {
        assertFalse(draftBeach.isFullyConfigured());
    }

    @Test
    void setActive_ThrowsException_WhenIncomplete() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> draftBeach.setActive(true));
        assertTrue(exception.getMessage().contains("Missing data"));
    }

    @Test
    void setActive_Succeeds_WhenFullyConfigured() {
        makeBeachFullyConfigured(draftBeach);
        assertDoesNotThrow(() -> draftBeach.setActive(true));
        assertTrue(draftBeach.isActive());
    }

    @Test
    void closeBeach_SetsStateCorrectly_AndBlocksUpdates() {
        draftBeach.closeBeach();
        assertTrue(draftBeach.isClosed());
        assertFalse(draftBeach.isActive());

        assertThrows(IllegalStateException.class, () -> draftBeach.updateExtraInfo("Test"));
        assertThrows(IllegalStateException.class, () -> draftBeach.closeBeach());
    }

    @Test
    void operations_ThrowException_WhenBeachIsActive() {
        makeBeachFullyConfigured(draftBeach);
        draftBeach.setActive(true);

        assertThrows(IllegalStateException.class, () -> draftBeach.updateGeneralInfo(createValidGeneral()));
        assertThrows(IllegalStateException.class, () -> draftBeach.addZone(Zone.create("Nuova Zona")));
        assertThrows(IllegalStateException.class, () -> draftBeach.removeSeason("Autunno"));
    }

    // ==========================================
    // TEST SU SETTERS E UPDATES
    // ==========================================

    @Test
    void updateExtraInfo_UpdatesSuccessfully_AndHandlesNullOrTooLong() {
        draftBeach.updateExtraInfo("extra info");
        assertEquals("extra info", draftBeach.getExtraInfo());

        draftBeach.updateExtraInfo(null);
        assertEquals("", draftBeach.getExtraInfo());

        String tooLong = "a".repeat(513);
        assertThrows(IllegalArgumentException.class, () -> draftBeach.updateExtraInfo(tooLong));
    }

    @Test
    void updateOwnerId_UpdatesSuccessfully_AndThrowsIfInvalid() {
        draftBeach.updateOwnerId(999);
        assertEquals(999, draftBeach.getOwnerId());

        assertThrows(IllegalArgumentException.class, () -> draftBeach.updateOwnerId(null));
        assertThrows(IllegalArgumentException.class, () -> draftBeach.updateOwnerId(0));
    }

    @Test
    void updateGeneralInfo_UpdatesSuccessfully_AndThrowsIfNull() {
        BeachGeneral newGen = new BeachGeneral("Nuova Spiaggia", "Descrizione", "+390558412343");
        draftBeach.updateGeneralInfo(newGen);
        assertEquals("Nuova Spiaggia", draftBeach.getBeachGeneral().name());

        assertThrows(IllegalArgumentException.class, () -> draftBeach.updateGeneralInfo(null));
    }

    @Test
    void updateInventory_UpdatesSuccessfully_AndThrowsIfNull() {
        BeachInventory newInv = BeachInventory.empty();
        draftBeach.updateInventory(newInv);
        assertNotNull(draftBeach.getBeachInventory());

        assertThrows(IllegalArgumentException.class, () -> draftBeach.updateInventory(null));
    }

    @Test
    void updateServices_UpdatesSuccessfully_AndThrowsIfNull() {
        BeachServices newSrv = BeachServices.none();
        draftBeach.updateServices(newSrv);
        assertNotNull(draftBeach.getBeachServices());

        assertThrows(IllegalArgumentException.class, () -> draftBeach.updateServices(null));
    }

    @Test
    void updateParking_UpdatesSuccessfully_AndThrowsIfNull() {
        Parking newPark = Parking.empty();
        draftBeach.updateParking(newPark);
        assertNotNull(draftBeach.getParking());

        assertThrows(IllegalArgumentException.class, () -> draftBeach.updateParking(null));
    }

    // ==========================================
    // 4. TEST SU STAGIONI
    // ==========================================

    @Test
    void addSeason_AutoExtractsZones_AndThrowsOnOverlap() {
        Season season1 = createValidSeason("Luglio", LocalDate.of(2030, 7, 1), LocalDate.of(2030, 7, 31), "Zona A");
        Season season2 = createValidSeason("Metà Luglio", LocalDate.of(2030, 7, 15), LocalDate.of(2030, 8, 15), "Zona B");

        draftBeach.addSeason(season1);
        assertEquals(1, draftBeach.getZones().size());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> draftBeach.addSeason(season2));
        assertTrue(ex.getMessage().contains("overlaps"));
    }

    @Test
    void addSeasons_AddsMultiple_AndThrowsIfAnyNull() {
        Season s1 = createValidSeason("S1", LocalDate.of(2030, 1, 1), LocalDate.of(2030, 1, 31), "Z1");
        Season s2 = createValidSeason("S2", LocalDate.of(2030, 2, 1), LocalDate.of(2030, 2, 28), "Z2");

        draftBeach.addSeasons(List.of(s1, s2));
        assertEquals(2, draftBeach.getSeasons().size());
        assertEquals(2, draftBeach.getZones().size());

        List<Season> invalidList = new ArrayList<>();
        invalidList.add(null);
        assertThrows(IllegalArgumentException.class, () -> draftBeach.addSeasons(invalidList));
    }

    @Test
    void updateSeason_ExtendsEndDate_Successfully() {
        Season season = createValidSeason("Estate", LocalDate.of(2030, 6, 1), LocalDate.of(2030, 8, 31), "Zona A");
        draftBeach.addSeason(season);

        LocalDate newEnd = LocalDate.of(2030, 9, 15);
        draftBeach.updateSeason("Estate", newEnd);

        assertEquals(newEnd, draftBeach.getSeasons().getFirst().endDate());
    }

    @Test
    void updateSeason_ThrowsException_IfShorteningEndDate_OrNotFound() {
        Season season = createValidSeason("Estate", LocalDate.of(2030, 6, 1), LocalDate.of(2030, 8, 31), "Zone A");
        draftBeach.addSeason(season);

        LocalDate shorterEnd = LocalDate.of(2030, 7, 31);
        assertThrows(IllegalArgumentException.class, () -> draftBeach.updateSeason("Estate", shorterEnd));
        assertThrows(IllegalArgumentException.class, () -> draftBeach.updateSeason("Stagione fantasma", LocalDate.of(2030, 12, 31)));
    }

    @Test
    void removeSeason_And_RemoveSeasons_Succeed_OrThrowIfNotFound() {
        Season s1 = createValidSeason("S1", LocalDate.of(2030, 1, 1), LocalDate.of(2030, 1, 31), "Z1");
        draftBeach.addSeason(s1);

        assertThrows(IllegalArgumentException.class, () -> draftBeach.removeSeason("inesistente"));
        assertThrows(IllegalArgumentException.class, () -> draftBeach.removeSeasons(List.of("S1", "inesistente")));

        draftBeach.removeSeason("S1");
        assertTrue(draftBeach.getSeasons().isEmpty());
    }

    // ==========================================
    // 5. TEST SU ZONE
    // ==========================================

    @Test
    void addZone_And_AddZones_ReplaceExisting() {
        Zone oldZone = Zone.create("Standard");
        draftBeach.addZone(oldZone);

        Zone updatedZone = oldZone.withName("Standard").withSpots(new ArrayList<>());
        Zone zone2 = Zone.create("VIP");

        draftBeach.addZones(List.of(updatedZone, zone2));
        assertEquals(2, draftBeach.getZones().size());
    }

    @Test
    void renameZone_RenamesSuccessfully_AndThrowsIfDuplicateOrNotFound() {
        draftBeach.addZone(Zone.create("Area 1"));
        draftBeach.addZone(Zone.create("Area 2"));

        assertThrows(IllegalArgumentException.class, () -> draftBeach.renameZone("Area 1", "Area 2"));
        assertThrows(IllegalArgumentException.class, () -> draftBeach.renameZone("inesistente", "nome fantasy"));

        draftBeach.renameZone("Area 1", "Area 1 VIP");
        assertTrue(draftBeach.getZones().stream().anyMatch(z -> z.name().equals("Area 1 VIP")));
    }

    @Test
    void removeZone_And_RemoveZones_Succeed_OrThrowIfNotFound() {
        Zone z1 = Zone.create("Z1");
        Zone z2 = Zone.create("Z2");
        draftBeach.addZones(List.of(z1, z2));

        assertThrows(IllegalArgumentException.class, () -> draftBeach.removeZone(Zone.create("fantasma")));
        assertThrows(IllegalArgumentException.class, () -> draftBeach.removeZones(List.of(z1, Zone.create("fantasma"))));

        draftBeach.removeZone(z1);
        draftBeach.removeZones(List.of(z2));
        assertTrue(draftBeach.getZones().isEmpty());
    }

    @Test
    void zoneOperations_ThrowException_IfLockedBySeason() {
        Season season = createValidSeason("Estate", LocalDate.now().plusDays(1), LocalDate.now().plusMonths(1), "Zona dentro Estate");
        draftBeach.addSeason(season);

        assertThrows(IllegalStateException.class, () -> draftBeach.removeZone(Zone.create("Zona dentro Estate")));
        assertThrows(IllegalStateException.class, () -> draftBeach.removeZones(List.of(Zone.create("Zona dentro Estate"))));
        assertThrows(IllegalStateException.class, () -> draftBeach.renameZone("Zona dentro Estate", "Marchino"));
        assertThrows(IllegalStateException.class, () -> draftBeach.addZone(Zone.create("Zona dentro Estate")));
    }

    // ==========================================
    // METODI PRIVATI PER CREAZIONE DATI
    // ==========================================

    private void makeBeachFullyConfigured(Beach beach) {
        beach.updateInventory(BeachInventory.empty());
        beach.updateServices(BeachServices.none());
        beach.updateParking(Parking.empty());
        beach.addSeason(createValidSeason("Autunno", LocalDate.now().plusDays(1), LocalDate.now().plusMonths(1), "Zona"));
    }

    private BeachGeneral createValidGeneral() {
        return new BeachGeneral("Spiaggia", "Spiaggia di Marchino", "+393331234567");
    }

    private Season createValidSeason(String name, LocalDate start, LocalDate end, String zoneName) {
        Pricing pricing = Pricing.create(10, 10, 10, 10, 10);
        List<ZoneTariff> tariffs = List.of(ZoneTariff.create(zoneName, 20, 30));
        return Season.builder()
                .name(name)
                .startDate(start)
                .endDate(end)
                .pricing(pricing)
                .zoneTariffs(tariffs)
                .build();
    }
}