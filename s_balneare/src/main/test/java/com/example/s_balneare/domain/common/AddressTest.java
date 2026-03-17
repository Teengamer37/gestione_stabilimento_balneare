package com.example.s_balneare.domain.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddressTest {
    // ==========================================
    // TEST DI VALIDAZIONE
    // ==========================================

    @Test
    void constructor_ThrowsException_IfFieldsAreBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new Address(1, null, "1", "City", "123", "IT"));
        assertThrows(IllegalArgumentException.class, () ->
                new Address(1, "Via dei Soldi", null, "City", "123", "IT"));
        assertThrows(IllegalArgumentException.class, () ->
                new Address(1, "Via dei Soldi", "1", null, "123", "IT"));
        assertThrows(IllegalArgumentException.class, () ->
                new Address(1, "Via dei Soldi", "1", "City", null, "IT"));
        assertThrows(IllegalArgumentException.class, () ->
                new Address(1, "Via dei Soldi", "1", "City", "123", null));

        assertThrows(IllegalArgumentException.class, () ->
                new Address(1, " ", "1", "City", "123", "IT"));
        assertThrows(IllegalArgumentException.class, () ->
                new Address(1, "Via dei Soldi", " ", "City", "123", "IT"));
        assertThrows(IllegalArgumentException.class, () ->
                new Address(1, "Via dei Soldi", "1", " ", "123", "IT"));
        assertThrows(IllegalArgumentException.class, () ->
                new Address(1, "Via dei Soldi", "1", "City", " ", "IT"));
        assertThrows(IllegalArgumentException.class, () ->
                new Address(1, "Via dei Soldi", "1", "City", "123", " "));
    }

    @Test
    void constructor_ThrowsException_IfFieldsExceedLimits() {
        assertThrows(IllegalArgumentException.class, () ->
                new Address(1, "a".repeat(256), "1", "City", "123", "IT"));
        assertThrows(IllegalArgumentException.class, () ->
                new Address(1, "Street", "12345678901", "City", "123", "IT"));
        assertThrows(IllegalArgumentException.class, () ->
                new Address(1, "Street", "1", "a".repeat(101), "123", "IT"));
        assertThrows(IllegalArgumentException.class, () ->
                new Address(1, "Street", "1", "City", "123".repeat(10), "IT"));
        assertThrows(IllegalArgumentException.class, () ->
                new Address(1, "Street", "1", "City", "123", "IT".repeat(60)));
    }

    // ==========================================
    // TEST FACTORY E BUILDER
    // ==========================================

    @Test
    void create_ReturnsAddressWithIdZero() {
        Address address = Address.create("Via Roma", "10", "Roma", "00100", "Italy");

        assertEquals(0, address.id());
        assertEquals("Via Roma", address.street());
    }

    @Test
    void builder_CreatesAddressCorrectly() {
        Address address = Address.builder()
                .street("Via Napoli")
                .streetNumber("2/A")
                .city("Napoli")
                .zipCode("80100")
                .country("Italy")
                .build();

        assertEquals("Via Napoli", address.street());
        assertEquals("2/A", address.streetNumber());
        assertEquals("Napoli", address.city());
        assertEquals("80100", address.zipCode());
        assertEquals("Italy", address.country());
    }

    @Test
    void builderCopyConstructor_CreatesIndependentCopy() {
        Address original = new Address(1, "Via Milano", "5", "Milano", "20100", "Italy");

        Address copy = Address.builder(original).build();
        assertEquals(original, copy);

        Address modified = Address.builder(original).street("Via Torino").build();
        assertNotEquals(original, modified);
        assertEquals("Via Milano", original.street());
    }

    // ==========================================
    // TEST METODI WITHER
    // ==========================================

    @Test
    void withers_ReturnNewInstance_AndKeepOriginalImmutable() {
        Address original = new Address(1, "Via dei Soldi", "1", "Prato", "59100", "IT");

        // Test withCity
        Address withNewCity = original.withCity("Firenze");
        assertEquals("Firenze", withNewCity.city());
        assertEquals("Prato", original.city());

        // Test withId
        Address withNewId = original.withId(99);
        assertEquals(99, withNewId.id());
        assertEquals(1, original.id());
    }

    // ==========================================
    // TEST DI UGUAGLIANZA
    // ==========================================

    @Test
    void equals_ReturnsTrueForSameValues() {
        Address a1 = Address.create("Via A", "1", "City", "123", "IT");
        Address a2 = Address.create("Via A", "1", "City", "123", "IT");

        assertEquals(a1, a2);

        Address a3 = a1.withStreet("Via B");
        assertNotEquals(a1, a3);
    }
}