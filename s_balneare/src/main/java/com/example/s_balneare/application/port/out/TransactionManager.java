package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.common.TransactionContext;

public interface TransactionManager {
    //esegue un blocco di codice all'interno di una transazione e restituisce un risultato
    <T> T executeInTransaction(TransactionCallable<T> callable);

    @FunctionalInterface
    interface TransactionCallable<T> {
        T execute(TransactionContext context) throws Exception;
    }
}