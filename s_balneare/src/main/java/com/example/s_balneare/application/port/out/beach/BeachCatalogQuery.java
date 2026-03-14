package com.example.s_balneare.application.port.out.beach;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.infrastructure.persistence.jdbc.beach.JdbcBeachCatalogQuery;
import com.example.s_balneare.infrastructure.persistence.jdbc.booking.JdbcAvailabilityQuery;

import java.util.List;

/**
 * Interfaccia che racchiude tutti i metodi che una Repository deve avere per interagire con un Database per estrarre
 * determinate spiagge secondo un parametro di ricerca.
 * Implementata in:
 * @see JdbcBeachCatalogQuery JdbcBeachCatalogQuery
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public interface BeachCatalogQuery {
    List<BeachSummary> searchActiveBeaches(String keyword, TransactionContext context);
}

/*
TODO:

// 1. Create a DTO for the detail page
public record BeachDetailPageDto(
    Beach beachData,          // You can reuse your findById method for this part
    double averageRating,     // SELECT AVG(rating) FROM reviews WHERE beachId = ?
    List<ReviewDto> latestReviews // SELECT * FROM reviews WHERE beachId = ? ORDER BY createdAt DESC LIMIT 10
) {}

// 2. Create the Query Port
public interface BeachDetailsQuery {
    BeachDetailPageDto getFullBeachDetails(Integer beachId);
}
 */