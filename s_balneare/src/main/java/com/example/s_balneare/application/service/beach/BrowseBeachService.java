package com.example.s_balneare.application.service.beach;

import com.example.s_balneare.application.port.in.beach.BrowseBeachUseCase;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachCatalogQuery;
import com.example.s_balneare.application.port.out.beach.BeachSummary;

import java.util.List;

/**
 * Servizio che implementa lo Use Case per la visualizzazione e la ricerca degli stabilimenti balneari attivi nel sistema.
 * <p>Usa BeachCatalogQuery per la ricerca delle spiagge nel DB.
 * <p>Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata.
 *
 * @see BrowseBeachUseCase BrowseBeachUseCase
 * @see BeachCatalogQuery BeachCatalogQuery
 * @see TransactionManager TransactionManager
 */
public class BrowseBeachService implements BrowseBeachUseCase {
    private final BeachCatalogQuery beachCatalogQuery;
    private final TransactionManager transactionManager;

    public BrowseBeachService(BeachCatalogQuery beachCatalogQuery, TransactionManager transactionManager) {
        this.beachCatalogQuery = beachCatalogQuery;
        this.transactionManager = transactionManager;
    }

    /**
     * Cerca nel database tutte le spiagge attive
     *
     * @return lista di spiagge attive
     * @see BeachSummary BeachSummary
     */
    @Override
    public List<BeachSummary> getActiveBeaches() {
        return transactionManager.executeInTransaction(context -> {
            return beachCatalogQuery.searchActiveBeaches(null, context);
        });
    }

    /**
     * Cerca nel database spiagge attive che rispettino il parametro di ricerca
     *
     * @param keyword Parametro di ricerca (città o paese)
     * @return lista di spiagge attive che rispettano il parametro di ricerca
     * @see BeachSummary BeachSummary
     */
    @Override
    public List<BeachSummary> searchActiveBeaches(String keyword) {
        return transactionManager.executeInTransaction(context -> {
            return beachCatalogQuery.searchActiveBeaches(keyword, context);
        });
    }
}