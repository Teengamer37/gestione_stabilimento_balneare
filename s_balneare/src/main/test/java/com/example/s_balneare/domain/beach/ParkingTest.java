package com.example.s_balneare.domain.beach;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingTest {
    // ==========================================
    // TEST DI VALIDAZIONE
    // ==========================================

    @Test
    void constructor_ThrowsException_IfAnyCountIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Parking(-1, 0, 0, false), "nAutoPark negative");
        assertThrows(IllegalArgumentException.class, () -> new Parking(0, -1, 0, false), "nMotoPark negative");
        assertThrows(IllegalArgumentException.class, () -> new Parking(0, 0, -1, false), "nElectricPark negative");
    }

    @Test
    void constructor_AllowsZeroValues() {
        assertDoesNotThrow(() -> new Parking(0, 0, 0, false));
    }

    // ==========================================
    // TEST FACTORY E BUILDER
    // ==========================================

    @Test
    void empty_CreatesParkingWithAllDefaults() {
        Parking parking = Parking.empty();

        assertEquals(0, parking.nAutoPark());
        assertEquals(0, parking.nMotoPark());
        assertEquals(0, parking.nElectricPark());
        assertFalse(parking.CCTV());
    }

    @Test
    void builder_CreatesParkingCorrectly() {
        Parking parking = Parking.builder()
                .nAutoPark(50)
                .nElectricPark(10)
                .CCTV(true)
                .build();

        assertEquals(50, parking.nAutoPark());
        assertEquals(10, parking.nElectricPark());
        assertTrue(parking.CCTV());
        assertEquals(0, parking.nMotoPark());
    }

    @Test
    void builderCopyConstructor_CreatesIndependentCopy() {
        Parking original = new Parking(10, 5, 2, true);

        Parking copy = Parking.builder(original).build();
        assertEquals(original, copy);

        Parking modified = Parking.builder(original).nAutoPark(99).build();
        assertNotEquals(original, modified);
        assertEquals(10, original.nAutoPark());
    }

    // ==========================================
    // TEST WITHER METHODS
    // ==========================================

    @Test
    void withers_ReturnNewInstance_AndKeepOriginalImmutable() {
        Parking original = new Parking(100, 50, 20, false);

        Parking updatedAuto = original.withNAutoPark(120);

        assertEquals(120, updatedAuto.nAutoPark());
        assertEquals(100, original.nAutoPark());

        Parking updatedCCTV = original.withCCTV(true);
        assertTrue(updatedCCTV.CCTV());
        assertFalse(original.CCTV());
    }

    @Test
    void withers_DoNotChangeOtherFields() {
        Parking original = new Parking(10, 10, 10, false);
        Parking updated = original.withNMotoPark(99);

        assertEquals(10, updated.nAutoPark());
        assertEquals(99, updated.nMotoPark());
        assertEquals(10, updated.nElectricPark());
        assertFalse(updated.CCTV());
    }

    // ==========================================
    // TEST DI UGUAGLIANZA
    // ==========================================

    @Test
    void equals_ReturnsTrue_ForSameValues() {
        Parking p1 = new Parking(50, 20, 10, true);
        Parking p2 = Parking.builder().nAutoPark(50).nMotoPark(20).nElectricPark(10).CCTV(true).build();

        assertEquals(p1, p2);

        Parking p3 = p1.withCCTV(false);
        assertNotEquals(p1, p3);
    }
}