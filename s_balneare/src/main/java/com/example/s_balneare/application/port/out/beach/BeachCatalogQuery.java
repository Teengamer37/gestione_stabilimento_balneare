package com.example.s_balneare.application.port.out.beach;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.infrastructure.persistence.jdbc.beach.JdbcBeachCatalogQuery;

import java.util.List;

/**
 * Interfaccia che racchiude tutti i metodi che una Repository deve avere per interagire con un Database per estrarre
 * determinate spiagge secondo un parametro di ricerca.
 * <p>Implementata in:
 *
 * @see JdbcBeachCatalogQuery JdbcBeachCatalogQuery
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public interface BeachCatalogQuery {
    List<BeachSummary> searchActiveBeaches(String keyword, TransactionContext context);
}