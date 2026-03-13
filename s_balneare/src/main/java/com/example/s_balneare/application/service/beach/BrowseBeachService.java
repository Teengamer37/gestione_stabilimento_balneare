package com.example.s_balneare.application.service.beach;

import com.example.s_balneare.application.port.in.beach.BrowseBeachUseCase;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.beach.BeachCatalogQuery;
import com.example.s_balneare.application.port.out.beach.BeachSummary;

import java.util.List;

public class BrowseBeachService implements BrowseBeachUseCase {
    private final BeachCatalogQuery beachCatalogQuery;
    private final TransactionManager transactionManager;

    public BrowseBeachService(BeachCatalogQuery beachCatalogQuery, TransactionManager transactionManager) {
        this.beachCatalogQuery = beachCatalogQuery;
        this.transactionManager = transactionManager;
    }

    @Override
    public List<BeachSummary> getActiveBeaches() {
        return transactionManager.executeInTransaction(context -> {
            return beachCatalogQuery.searchActiveBeaches(null, context);
        });
    }

    @Override
    public List<BeachSummary> searchActiveBeaches(String keyword) {
        return transactionManager.executeInTransaction(context -> {
            return beachCatalogQuery.searchActiveBeaches(keyword, context);
        });
    }
}