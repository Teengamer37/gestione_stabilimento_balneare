package com.example.s_balneare.application.port.out.beach;

import java.time.Instant;

/**
 * Record che rappresenta una recensione da parte di un utente ad una determinata spiaggia.<br>
 * Usata in:
 *
 * @see BeachReviewsQuery BeachReviewsQuery
 */
public record BeachReviewDto(
        Integer reviewId,
        String customerName,
        String customerSurname,
        int rating,
        String comment,
        Instant createdAt
) {}