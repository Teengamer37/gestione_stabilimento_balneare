package com.example.s_balneare.application.port.out.beach;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.infrastructure.persistence.jdbc.beach.JdbcBeachReviewsQuery;

import java.util.List;

/**
 * Interfaccia che racchiude tutti i metodi che una Repository deve avere per interagire con un Database per estrarre
 * tutte le review di una determinata spiaggia.<br>
 * Implementata in:
 *
 * @see JdbcBeachReviewsQuery JdbcBeachCatalogQuery
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public interface BeachReviewsQuery {
    //prende tutte le recensioni di una spiaggia, con aggiunta di nome e cognome dell'utente
    List<BeachReviewDto> getReviewsByBeachId(Integer beachId, TransactionContext context);

    //calcola la valutazione media della spiaggia
    double getAverageRating(Integer beachId, TransactionContext context);
}