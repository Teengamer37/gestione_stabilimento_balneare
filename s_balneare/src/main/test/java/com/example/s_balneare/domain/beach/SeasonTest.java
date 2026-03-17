package com.example.s_balneare.domain.beach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SeasonTest {
    private Pricing validPricing;
    private List<ZoneTariff> validTariffs;
    private LocalDate validStart;
    private LocalDate validEnd;

    @BeforeEach
    void setUp() {
        //inizializzo dati validi da riutilizzare nei test
        validPricing = Pricing.create(10, 8, 5, 15, 20);
        validTariffs = List.of(ZoneTariff.create("Prima Fila", 30, 50));
        validStart = LocalDate.of(2030, 6, 1);
        validEnd = LocalDate.of(2030, 8, 31);
    }

    // ==========================================
    // TEST DI VALIDAZIONE
    // ==========================================

    @Test
    void constructor_ThrowsException_IfNameIsBlankOrTooLong() {
        assertThrows(IllegalArgumentException.class, () -> new Season("   ", validStart, validEnd, validPricing, validTariffs), "nome vuoto");

        String tooLongName = "a".repeat(51);
        assertThrows(IllegalArgumentException.class, () -> new Season(tooLongName, validStart, validEnd, validPricing, validTariffs), "nome troppo lungo");
    }

    @Test
    void constructor_ThrowsException_IfDatesAreInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new Season("Estate", null, validEnd, validPricing, validTariffs), "data inizio = null");
        assertThrows(IllegalArgumentException.class, () -> new Season("Estate", validStart, null, validPricing, validTariffs), "data fine = null");

        LocalDate invalidEnd = validStart.minusDays(1);
        assertThrows(IllegalArgumentException.class, () -> new Season("Estate", validStart, invalidEnd, validPricing, validTariffs), "data fine < data inizio");

        assertThrows(IllegalArgumentException.class, () -> new Season("Estate", validStart, validStart, validPricing, validTariffs), "date uguali");
    }

    @Test
    void constructor_ThrowsException_IfPricingIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Season("Estate", validStart, validEnd, null, validTariffs));
    }

    @Test
    void constructor_ThrowsException_IfZoneTariffsAreInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new Season("Estate", validStart, validEnd, validPricing, null), "lista tariffe = null");
        assertThrows(IllegalArgumentException.class, () -> new Season("Estate", validStart, validEnd, validPricing, new ArrayList<>()), "lista tariffe vuota");

        List<ZoneTariff> listWithNull = new ArrayList<>();
        listWithNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> new Season("Estate", validStart, validEnd, validPricing, listWithNull), "lista tariffe con elemento = null");
    }

    // ==========================================
    // TEST METODO DI BUSINESS: includes()
    // ==========================================

    @Test
    void includes_ReturnsTrue_ForDatesWithinRange() {
        Season season = new Season("Estate", validStart, validEnd, validPricing, validTariffs);

        assertTrue(season.includes(validStart), "data di inizio dovrebbe essere inclusa");
        assertTrue(season.includes(validEnd), "data di fine dovrebbe essere inclusa");
        assertTrue(season.includes(LocalDate.of(2030, 7, 15)), "data nel mezzo dovrebbe essere inclusa");
    }

    @Test
    void includes_ReturnsFalse_ForDatesOutsideRange() {
        Season season = new Season("Estate", validStart, validEnd, validPricing, validTariffs);

        assertFalse(season.includes(validStart.minusDays(1)), "data prima dell'inizio dovrebbe essere esclusa");
        assertFalse(season.includes(validEnd.plusDays(1)), "data dopo la fine dovrebbe essere esclusa");
        assertFalse(season.includes(null), "data nulla dovrebbe ritornare false");
    }

    // ==========================================
    // TEST BUILDER E WITHER
    // ==========================================

    @Test
    void builder_CreatesSeasonCorrectly() {
        Season season = Season.builder()
                .name("Inverno")
                .startDate(LocalDate.of(2030, 12, 1))
                .endDate(LocalDate.of(2031, 2, 28))
                .pricing(validPricing)
                .zoneTariffs(validTariffs)
                .build();

        assertEquals("Inverno", season.name());
        assertEquals(validPricing, season.pricing());
        assertEquals(1, season.zoneTariffs().size());
    }

    @Test
    void builderCopyConstructor_CreatesIndependentCopy() {
        Season original = new Season("Marco", validStart, validEnd, validPricing, validTariffs);
        Season copy = Season.builder(original).build();

        assertEquals(original, copy);

        Season modified = Season.builder(original).name("Marchino").build();
        assertNotEquals(original, modified);
        assertEquals("Marco", original.name());
    }

    @Test
    void withers_ReturnNewInstance_AndKeepOriginalImmutable() {
        Season original = new Season("Estate", validStart, validEnd, validPricing, validTariffs);

        LocalDate newEnd = validEnd.plusMonths(1);
        Season extended = original.withDates(original.startDate(), newEnd);

        assertEquals(newEnd, extended.endDate());
        assertEquals(validEnd, original.endDate());

        Pricing newPricing = Pricing.create(99, 99, 99, 99, 99);
        Season updatedPricing = original.withPricing(newPricing);

        assertEquals(newPricing, updatedPricing.pricing());
        assertEquals(validPricing, original.pricing());
    }
}