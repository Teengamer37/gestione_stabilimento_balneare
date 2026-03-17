package com.example.s_balneare.domain.beach;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ZoneTariffTest {
    // ==========================================
    // TEST DI VALIDAZIONE
    // ==========================================

    @Test
    void constructor_ThrowsException_IfZoneNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new ZoneTariff(null, 20.0, 40.0));
        assertThrows(IllegalArgumentException.class, () -> new ZoneTariff("   ", 20.0, 40.0));
    }

    @Test
    void constructor_ThrowsException_IfPricesAreNegative() {
        assertThrows(IllegalArgumentException.class, () -> new ZoneTariff("Prima Fila", -1.0, 40.0));
        assertThrows(IllegalArgumentException.class, () -> new ZoneTariff("Prima Fila", 20.0, -1.0));
    }

    @Test
    void constructor_AllowsZeroValuesForPrices() {
        assertDoesNotThrow(() -> new ZoneTariff("Area Gratuita", 0, 0));
    }

    // ==========================================
    // TEST FACTORY E BUILDER
    // ==========================================

    @Test
    void create_ReturnsCorrectInstance() {
        ZoneTariff tariff = ZoneTariff.create("Area VIP", 50.0, 100.0);

        assertEquals("Area VIP", tariff.zoneName());
        assertEquals(50.0, tariff.priceOmbrellone());
        assertEquals(100.0, tariff.priceTenda());
    }

    @Test
    void builder_CreatesInstanceCorrectly() {
        ZoneTariff tariff = ZoneTariff.builder()
                .zoneName("Standard")
                .priceOmbrellone(25.0)
                .priceTenda(45.0)
                .build();

        assertEquals("Standard", tariff.zoneName());
        assertEquals(25.0, tariff.priceOmbrellone());
        assertEquals(45.0, tariff.priceTenda());
    }

    @Test
    void builderCopyConstructor_CreatesIndependentCopy() {
        ZoneTariff original = ZoneTariff.create("Marco", 10, 20);

        ZoneTariff copy = ZoneTariff.builder(original).build();
        assertEquals(original, copy);

        ZoneTariff modified = ZoneTariff.builder(original).priceTenda(99).build();
        assertNotEquals(original, modified);
        assertEquals(20, original.priceTenda());
    }

    // ==========================================
    // TEST WITHER METHODS
    // ==========================================

    @Test
    void withers_ReturnNewInstance_AndKeepOriginalImmutable() {
        ZoneTariff original = new ZoneTariff("Standard", 15.0, 30.0);

        ZoneTariff renamed = original.withZoneName("Premium");
        assertEquals("Premium", renamed.zoneName());
        assertEquals("Standard", original.zoneName(), "l'originale deve rimanere immutato");

        ZoneTariff priceUpdated = original.withPriceOmbrellone(20.0);
        assertEquals(20.0, priceUpdated.priceOmbrellone());
        assertEquals(15.0, original.priceOmbrellone(), "l'originale deve rimanere immutato");
    }

    // ==========================================
    // TEST DI UGUAGLIANZA
    // ==========================================

    @Test
    void equals_ReturnsTrueForSameValues_AndFalseForDifferentValues() {
        ZoneTariff zt1 = ZoneTariff.create("Prima Fila", 30, 60);
        ZoneTariff zt2 = ZoneTariff.builder().zoneName("Prima Fila").priceOmbrellone(30).priceTenda(60).build();
        assertEquals(zt1, zt2);

        ZoneTariff zt3 = zt1.withPriceTenda(65);
        assertNotEquals(zt1, zt3);
    }
}