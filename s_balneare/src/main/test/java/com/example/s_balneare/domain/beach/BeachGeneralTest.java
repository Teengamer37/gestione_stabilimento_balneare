package com.example.s_balneare.domain.beach;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeachGeneralTest {
    // ==========================================
    // TEST DEL COSTRUTTORE
    // ==========================================

    @Test
    void constructor_CreatesObjectCorrectly_WithValidData() {
        BeachGeneral general = new BeachGeneral("Lido Bello", "Un lido molto bello", "+393331234567");

        assertEquals("Lido Bello", general.name());
        assertEquals("Un lido molto bello", general.description());
        assertEquals("+393331234567", general.phoneNumber());
    }

    @Test
    void constructor_RemovesWhitespacesFromPhoneNumber() {
        BeachGeneral general = new BeachGeneral("Lido Bello", "Descrizione", "+39 333 123 4567");

        assertEquals("+393331234567", general.phoneNumber(), "spaces removed correctly");
    }

    // ==========================================
    // TEST DI VALIDAZIONE: NAME
    // ==========================================

    @Test
    void constructor_ThrowsException_IfNameIsBlank() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new BeachGeneral("   ", "Descrizione", "+39123456")
        );
        assertTrue(ex.getMessage().contains("name cannot be blank"));
    }

    @Test
    void constructor_ThrowsException_IfNameIsTooLong() {
        String tooLongName = "a".repeat(101);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new BeachGeneral(tooLongName, "Descrizione", "+39123456")
        );
        assertTrue(ex.getMessage().contains("name cannot exceed 100 characters"));
    }

    @Test
    void constructor_ThrowsException_IfNameIsNull() {
        assertThrows(NullPointerException.class, () ->
                new BeachGeneral(null, "Descrizione", "+39123456")
        );
    }

    // ==========================================
    // TEST DI VALIDAZIONE: DESCRIPTION
    // ==========================================

    @Test
    void constructor_ThrowsException_IfDescriptionIsTooLong() {
        String tooLongDescription = "a".repeat(513);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new BeachGeneral("Lido", tooLongDescription, "+39123456")
        );
        assertTrue(ex.getMessage().contains("description cannot exceed 512 characters"));
    }

    @Test
    void constructor_ThrowsException_IfDescriptionIsNull() {
        assertThrows(NullPointerException.class, () ->
                new BeachGeneral("Lido", null, "+39123456")
        );
    }

    // ==========================================
    // TEST DI VALIDAZIONE: PHONE NUMBER
    // ==========================================

    @Test
    void constructor_ThrowsException_IfPhoneNumberIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new BeachGeneral("Lido", "Descrizione", null)
        );
        assertTrue(ex.getMessage().contains("phoneNumber cannot be null"));
    }

    @Test
    void constructor_ThrowsException_IfPhoneNumberIsBlankAfterCleaning() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new BeachGeneral("Lido", "Descrizione", "   ")
        );
        assertTrue(ex.getMessage().contains("phoneNumber cannot be blank"));
    }

    @Test
    void constructor_ThrowsException_IfPhoneNumberIsTooLong() {
        String tooLongPhone = "+" + "1".repeat(50);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new BeachGeneral("Lido", "Descrizione", tooLongPhone)
        );
        assertTrue(ex.getMessage().contains("phoneNumber cannot exceed 50 characters"));
    }

    @Test
    void constructor_ThrowsException_IfPhoneNumberIsInvalid_WithoutPlus() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new BeachGeneral("Lido", "Descrizione", "393331234567")
        );
        assertTrue(ex.getMessage().contains("phoneNumber not valid"));
    }

    @Test
    void constructor_ThrowsException_IfPhoneNumberIsInvalid_WithLetters() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new BeachGeneral("Lido", "Descrizione", "+39333ABC456")
        );
        assertTrue(ex.getMessage().contains("phoneNumber not valid"));
    }

    // ==========================================
    // TEST DEI METODI WITHER
    // ==========================================

    @Test
    void withName_CreatesNewObject_WithUpdatedName() {
        BeachGeneral original = new BeachGeneral("Marco", "Descrizione", "+39123");

        BeachGeneral updated = original.withName("Marchino");

        assertEquals("Marchino", updated.name());
        assertEquals("Descrizione", updated.description());

        assertEquals("Marco", original.name());
    }

    @Test
    void withDescription_CreatesNewObject_WithUpdatedDescription() {
        BeachGeneral original = new BeachGeneral("Lido", "Descrizione", "+39123");

        BeachGeneral updated = original.withDescription("Descrizioncina");

        assertEquals("Descrizioncina", updated.description());
        assertEquals("Lido", updated.name());
    }

    @Test
    void withPhoneNumber_CreatesNewObject_WithUpdatedPhoneNumber_AndCleansWhitespaces() {
        BeachGeneral original = new BeachGeneral("Lido", "Descrizione", "+39123");

        BeachGeneral updated = original.withPhoneNumber("+44 987 654");

        assertEquals("+44987654", updated.phoneNumber());
        assertEquals("Lido", updated.name());
    }
}