package com.example.s_balneare.domain.moderation;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.*;

class ReportTest {
    //helper per creare dati validi
    private final Instant now = Instant.now();

    // ==========================================
    // TEST DI VALIDAZIONE
    // ==========================================

    @Test
    void constructor_CreatesValidReport_WhenAllDataIsCorrect() {
        assertDoesNotThrow(() -> new Report(1, 10, 20, ReportTargetType.USER, "Descrizione valida", now, ReportStatus.PENDING, 500));
    }

    @Test
    void constructor_ThrowsException_IfReporterEqualsReported() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new Report(1, 10, 10, ReportTargetType.USER, "Descrizione anch'essa valida", now, ReportStatus.PENDING, 500)
        );
        assertTrue(ex.getMessage().contains("cannot be the same user"));
    }

    @Test
    void constructor_ThrowsException_IfIdsAreInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new Report(1, 0, 20, ReportTargetType.USER, "Non si è presentato alle prenotazioni", now, ReportStatus.PENDING, 500));
        assertThrows(IllegalArgumentException.class, () ->
                new Report(1, 10, null, ReportTargetType.USER, "Non ha pagato", now, ReportStatus.PENDING, 500));
    }

    @Test
    void constructor_ThrowsException_IfDescriptionIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new Report(1, 10, 20, ReportTargetType.USER, "   ", now, ReportStatus.PENDING, 500));

        String longDesc = "a".repeat(1025);
        assertThrows(IllegalArgumentException.class, () ->
                new Report(1, 10, 20, ReportTargetType.USER, longDesc, now, ReportStatus.PENDING, 500));
    }

    @Test
    void constructor_ThrowsException_IfCreatedAtIsInFuture() {
        Instant future = now.plus(1, ChronoUnit.HOURS);
        assertThrows(IllegalArgumentException.class, () ->
                new Report(1, 10, 20, ReportTargetType.USER, "Boh non mi stava simpatico", future, ReportStatus.PENDING, 500)
        );
    }

    @Test
    void constructor_ThrowsException_IfBookingIdIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Report(1, 10, 20, ReportTargetType.USER, "Per quel naso che aveva non lo voglio più vedere", now, ReportStatus.PENDING, null)
        );
    }

    // ==========================================
    // TEST METODI BUSINESS
    // ==========================================

    @Test
    void approve_ChangesStatus_WhenPending() {
        Report report = new Report(1, 10, 20, ReportTargetType.USER, "Ascoltava Taylor Swift in spiaggia", now, ReportStatus.PENDING, 500);
        report.approve();
        assertEquals(ReportStatus.APPROVED, report.getStatus());
    }

    @Test
    void approve_ThrowsException_WhenAlreadyProcessed() {
        Report report = new Report(1, 10, 20, ReportTargetType.USER, "Ha offeso tutto lo staff", now, ReportStatus.APPROVED, 500);

        assertThrows(IllegalStateException.class, report::approve);
    }

    @Test
    void reject_ChangesStatus_WhenPending() {
        Report report = new Report(1, 10, 20, ReportTargetType.USER, "Hanno deciso di fare una grigliata in spiaggia", now, ReportStatus.PENDING, 500);
        report.reject();
        assertEquals(ReportStatus.REJECTED, report.getStatus());
    }

    @Test
    void reject_ThrowsException_WhenAlreadyProcessed() {
        Report report = new Report(1, 10, 20, ReportTargetType.USER, "Vengono con un Diesel quando hanno prenotato un posto per veicoli elettrici", now, ReportStatus.REJECTED, 500);
        
        assertThrows(IllegalStateException.class, report::reject);
    }
}