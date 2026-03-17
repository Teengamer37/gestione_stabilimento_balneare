package com.example.s_balneare.domain.beach;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeachServicesTest {
    // ==========================================
    // TEST FACTORY E DEFAULT
    // ==========================================

    @Test
    void none_CreatesAllFalseInstance() {
        BeachServices services = BeachServices.none();

        assertFalse(services.bathrooms());
        assertFalse(services.showers());
        assertFalse(services.pool());
        assertFalse(services.bar());
        assertFalse(services.restaurant());
        assertFalse(services.wifi());
        assertFalse(services.volleyballField());
    }

    // ==========================================
    // TEST BUILDER
    // ==========================================

    @Test
    void builder_SetsValuesCorrectly() {
        BeachServices services = BeachServices.builder()
                .bathrooms(true)
                .pool(true)
                .wifi(true)
                .build();

        assertTrue(services.bathrooms());
        assertTrue(services.pool());
        assertTrue(services.wifi());
        assertFalse(services.showers());
        assertFalse(services.bar());
        assertFalse(services.restaurant());
        assertFalse(services.volleyballField());
    }

    @Test
    void builderCopyConstructor_CreatesIndependentCopy() {
        BeachServices original = new BeachServices(true, false, true, false, false, true, false);

        BeachServices copy = BeachServices.builder(original).build();

        assertEquals(original, copy);

        BeachServices modified = BeachServices.builder(original).wifi(false).build();
        assertNotEquals(original, modified);
        assertTrue(original.wifi());
    }

    // ==========================================
    // TEST WITHER METHODS
    // ==========================================

    @Test
    void withers_ReturnNewInstance_AndKeepOriginalImmutable() {
        BeachServices original = BeachServices.none();

        BeachServices withBathrooms = original.withBathrooms(true);
        assertTrue(withBathrooms.bathrooms());
        assertFalse(original.bathrooms());

        BeachServices withWifi = original.withWifi(true);
        assertTrue(withWifi.wifi());
        assertFalse(original.wifi());
    }

    @Test
    void withers_ChainEffectively() {
        BeachServices base = BeachServices.none();
        BeachServices fullService = base.withBathrooms(true).withShowers(true).withBar(true);

        assertTrue(fullService.bathrooms());
        assertTrue(fullService.showers());
        assertTrue(fullService.bar());
        assertFalse(fullService.pool());
    }

    // ==========================================
    // TEST DI UGUAGLIANZA
    // ==========================================

    @Test
    void equals_ReturnsTrue_ForSameValues() {
        BeachServices s1 = BeachServices.builder().wifi(true).pool(true).build();
        BeachServices s2 = BeachServices.builder().wifi(true).pool(true).build();

        assertEquals(s1, s2);
    }
}