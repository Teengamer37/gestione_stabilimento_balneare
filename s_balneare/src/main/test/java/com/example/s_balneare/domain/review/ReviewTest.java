package com.example.s_balneare.domain.review;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {
    //helper per creare dati validi
    private final Instant NOW = Instant.now();

    // ==========================================
    // TEST DI VALIDAZIONE
    // ==========================================

    @Test
    void constructor_CreatesValidReview_WithCorrectData() {
        assertDoesNotThrow(() -> new Review(1, 10, 20, 5, "Spiaggia fantastica!", NOW));
    }

    @Test
    void constructor_ThrowsException_IfBeachIdIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new Review(1, null, 20, 5, "Mare pulitissimo, consiglio", NOW));
        assertThrows(IllegalArgumentException.class, () ->
                new Review(1, 0, 20, 2, "Non ci hanno permesso di fare una grigliata...", NOW));
        assertThrows(IllegalArgumentException.class, () ->
                new Review(1, -1, 20, 1, "Ci hanno rigato la macchina", NOW));
    }

    @Test
    void constructor_ThrowsException_IfCustomerIdIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new Review(1, 10, null, 5, "Commento anonimo muhahahaha", NOW), "CustomerId null");
        assertThrows(IllegalArgumentException.class, () ->
                new Review(1, 10, -5, 5, "boh spiaggia ok, penso (mi hanno obbligato a fare la recensione)", NOW));
    }

    @Test
    void constructor_ThrowsException_IfRatingIsOutOfRange() {
        assertThrows(IllegalArgumentException.class, () ->
                new Review(1, 10, 20, 0, "FA DAVVERO VOMITARE CHE SCHIFO STA SPIAGGIA", NOW));
        assertThrows(IllegalArgumentException.class, () ->
                new Review(1, 10, 20, 6, "FAVOLOSA, CI HANNO OFFERTO TUTTOOO!!! CONSIGLIO VIVAMENTE", NOW));
    }

    @Test
    void constructor_ThrowsException_IfCommentIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new Review(1, 10, 20, 5, "", NOW));
        assertThrows(IllegalArgumentException.class, () ->
                new Review(1, 10, 20, 5, "   ", NOW));
        assertThrows(IllegalArgumentException.class, () ->
                new Review(1, 10, 20, 5, null, NOW));

        String tooLongComment = "marco".repeat(250);
        assertThrows(IllegalArgumentException.class, () ->
                new Review(1, 10, 20, 5, tooLongComment, NOW));
    }

    @Test
    void constructor_ThrowsException_IfCreatedAtIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Review(1, 10, 20, 5, "Hanno un Wi-Fi meglio di quello a casa mia", null));
    }

    // ==========================================
    // TEST DI LOGICA AGGIUNTIVA
    // ==========================================

    @Test
    void comment_IsTrimmedCorrectly() {
        String messyComment = "  bro, fuoco come posto, consiglio a tutti!  ";
        Review review = new Review(1, 10, 20, 5, messyComment, NOW);

        assertEquals("bro, fuoco come posto, consiglio a tutti!", review.getComment());
    }
}