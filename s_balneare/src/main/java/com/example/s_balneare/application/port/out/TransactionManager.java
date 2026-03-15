package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.common.TransactionContext;

/**
 * Interfaccia che ha il compito di semplificare l'interazione tra i Service e il DB.
 * <p>Permette di avviare una SQL Transaction senza però sapere che libreria viene usata per l'interazione (JDBC, Spring...)
 * <p>Implementata in:
 *
 * @see com.example.s_balneare.infrastructure.persistence.jdbc.JdbcTransactionManager JdbcTransactionManager
 */
public interface TransactionManager {
    //esegue un blocco di codice all'interno di una transazione e restituisce un risultato
    <T> T executeInTransaction(TransactionCallable<T> callable);

    //esegue un blocco di codice all'interno di una transazione senza restituire alcun risultato
    void executeInTransaction(TransactionRunnable runnable);

    //usato per eseguire transazioni che restituiscono risultati
    @FunctionalInterface
    interface TransactionCallable<T> {
        T execute(TransactionContext context) throws Exception;
    }

    //usato per eseguire transazioni che NON restituiscono risultati
    @FunctionalInterface
    interface TransactionRunnable {
        void execute(TransactionContext context) throws Exception;
    }
}