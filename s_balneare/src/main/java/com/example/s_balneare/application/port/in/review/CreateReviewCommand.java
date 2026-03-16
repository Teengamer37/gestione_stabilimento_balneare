package com.example.s_balneare.application.port.in.review;

/**
 * Record che prende come parametri tutti gli attributi di Review per la creazione di una recensione su una Beach da parte del Customer.<br>
 * Usato in:
 *
 * @see ReviewUseCase ReviewUseCase
 */
public record CreateReviewCommand(
        Integer beachId,
        Integer customerId,
        int rating,
        String comment
) {}