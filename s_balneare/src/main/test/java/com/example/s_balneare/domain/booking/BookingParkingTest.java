package com.example.s_balneare.domain.booking;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookingParkingTest {
    // ==========================================
    // TEST DEL COSTRUTTORE E VALIDAZIONE
    // ==========================================

    @Test
    void constructor_ThrowsException_IfAnyValueIsNegative() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> new BookingParking(-1, 0, 0));
        assertTrue(ex1.getMessage().contains("negative"));

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> new BookingParking(0, -1, 0));
        assertTrue(ex2.getMessage().contains("negative"));

        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, () -> new BookingParking(0, 0, -1));
        assertTrue(ex3.getMessage().contains("negative"));
    }

    @Test
    void constructor_AllowsZeroAndPositiveValues() {
        assertDoesNotThrow(() -> new BookingParking(0, 0, 0));
        assertDoesNotThrow(() -> new BookingParking(1, 2, 3));
    }

    // ==========================================
    // TEST FACTORY
    // ==========================================

    @Test
    void empty_CreatesObjectWithAllZeros() {
        BookingParking parking = BookingParking.empty();

        assertEquals(0, parking.autoPark());
        assertEquals(0, parking.motoPark());
        assertEquals(0, parking.electricPark());
    }

    // ==========================================
    // TEST METODI WITHER
    // ==========================================

    @Test
    void withAutoPark_ReturnsNewInstance_AndKeepsOriginalImmutable() {
        BookingParking original = new BookingParking(1, 1, 1);

        BookingParking updated = original.withAutoPark(5);

        assertEquals(5, updated.autoPark());
        assertEquals(1, updated.motoPark());

        assertEquals(1, original.autoPark());
    }

    @Test
    void withMotoPark_ReturnsNewInstance_AndKeepsOriginalImmutable() {
        BookingParking original = new BookingParking(1, 1, 1);

        BookingParking updated = original.withMotoPark(5);

        assertEquals(5, updated.motoPark());
        assertEquals(1, updated.electricPark());
        assertEquals(1, original.motoPark());
    }

    @Test
    void withElectricPark_ReturnsNewInstance_AndKeepsOriginalImmutable() {
        BookingParking original = new BookingParking(1, 1, 1);

        BookingParking updated = original.withElectricPark(5);

        assertEquals(5, updated.electricPark());
        assertEquals(1, updated.autoPark());
        assertEquals(1, original.electricPark());
    }

    // ==========================================
    // TEST BUILDER
    // ==========================================

    @Test
    void builder_BuildsCorrectInstance() {
        BookingParking parking = BookingParking.builder()
                .autoPark(10)
                .electricPark(2)
                .build();

        assertEquals(10, parking.autoPark());
        assertEquals(0, parking.motoPark());
        assertEquals(2, parking.electricPark());
    }

    @Test
    void builderCopyConstructor_CreatesIndependentCopy() {
        BookingParking original = new BookingParking(2, 4, 6);

        BookingParking copy = BookingParking.builder().autoPark(original.autoPark())
                .motoPark(original.motoPark())
                .electricPark(original.electricPark())
                .build();

        assertEquals(original, copy);

        BookingParking modified = BookingParking.builder(original).motoPark(10).build();

        assertNotEquals(original, modified);
        assertEquals(4, original.motoPark());
        assertEquals(10, modified.motoPark());
    }

    // ==========================================
    // TEST UGUAGLIANZA
    // ==========================================

    @Test
    void equals_ReturnsTrueForIdenticalValues() {
        BookingParking p1 = new BookingParking(5, 2, 1);
        BookingParking p2 = new BookingParking(5, 2, 1);

        assertEquals(p1, p2);

        BookingParking p3 = p1.withAutoPark(6);
        assertNotEquals(p1, p3);
    }
}