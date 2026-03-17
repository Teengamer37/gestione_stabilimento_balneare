package com.example.s_balneare.domain.moderation;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class BanTest {
    //helper per creare dati validi
    private static final Instant NOW = Instant.now();

    // ==========================================
    // TEST DI VALIDAZIONE
    // ==========================================

    @Test
    void constructor_CreatesValidBeachBan() {
        Ban ban = new Ban(1, 100, BanType.BEACH, 5, 1, "Violazione regole della spiaggia", NOW);
        assertEquals(BanType.BEACH, ban.banType());
        assertEquals(5, ban.bannedFromBeachId());
    }

    @Test
    void constructor_CreatesValidApplicationBan() {
        Ban ban = new Ban(1, 100, BanType.APPLICATION, null, 1, "Non hai rispettato il Culto del Marco", NOW);
        assertEquals(BanType.APPLICATION, ban.banType());
        assertNull(ban.bannedFromBeachId());
    }

    @Test
    void constructor_ThrowsException_IfBannedIdIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new Ban(1, 0, BanType.APPLICATION, null, 1, "Troppo imbarazzante per scriverlo... hahahahahahaha", NOW));
        assertThrows(IllegalArgumentException.class, () ->
                new Ban(1, null, BanType.APPLICATION, null, 1, "300 booking in 300 giorni???", NOW));
    }

    @Test
    void constructor_ThrowsException_IfBeachBanHasNoBeachId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Ban(1, 100, BanType.BEACH, null, 1, "Non ti sei presentato alla spiaggia", NOW));
        assertThrows(IllegalArgumentException.class, () ->
                new Ban(1, 100, BanType.BEACH, -5, 1, "Hai dato dello scemo al gestore", NOW));
    }

    @Test
    void constructor_ThrowsException_IfAppBanHasBeachId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Ban(1, 100, BanType.APPLICATION, 5, 1, "Nome utente offensivo", NOW));
    }

    @Test
    void constructor_ThrowsException_IfAdminIdIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new Ban(1, 100, BanType.APPLICATION, null, null, "Nome utente vergognoso", NOW));
    }

    @Test
    void constructor_ThrowsException_IfReasonIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new Ban(1, 100, BanType.APPLICATION, null, 1, "", NOW));
        assertThrows(IllegalArgumentException.class, () ->
                new Ban(1, 100, BanType.APPLICATION, null, 1, "   ", NOW));
    }

    @Test
    void constructor_ThrowsException_IfCreatedAtIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Ban(1, 100, BanType.APPLICATION, null, 1, "Boh mi andava di bagnarti", null));
    }

    // ==========================================
    // TEST DI UGUAGLIANZA
    // ==========================================

    @Test
    void equals_ReturnsTrueForSameValues() {
        Ban b1 = new Ban(1, 100, BanType.BEACH, 5, 1, "Stesso motivo", NOW);
        Ban b2 = new Ban(1, 100, BanType.BEACH, 5, 1, "Stesso motivo", NOW);

        assertEquals(b1, b2);

        Ban b3 = new Ban(2, 100, BanType.BEACH, 5, 1, "Motivo stesso", NOW);
        assertNotEquals(b1, b3);
    }
}