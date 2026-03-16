package com.example.s_balneare.application.port.in.moderation;

/**
 * Record che prende come parametri l'ID dell'utente creatore del report, la descrizione del report e il riferimento<br>
 * al Booking per il quale si fa la segnalazione: tutto il necessario per la creazione di un report.<br>
 * Usato in:
 *
 * @see CreateReportUseCase CreateReportUseCase
 */
public record CreateReportCommand(
        Integer reporterId,
        String description,
        Integer bookingId
) {}