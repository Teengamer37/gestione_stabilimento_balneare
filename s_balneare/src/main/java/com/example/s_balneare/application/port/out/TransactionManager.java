package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.common.TransactionContext;

/**
 * Interfaccia che ha il compito di semplificare l'interazione tra i Service e il DB.
 * Permette di avviare una SQL Transaction senza però sapere che libreria viene usata per l'interazione (JDBC, Spring...)
 * Implementata in:
 * @see com.example.s_balneare.infrastructure.persistence.jdbc.JdbcTransactionManager JdbcTransactionManager
 */
public interface TransactionManager {
    //esegue un blocco di codice all'interno di una transazione e restituisce un risultato
    <T> T executeInTransaction(TransactionCallable<T> callable);

    @FunctionalInterface
    interface TransactionCallable<T> {
        T execute(TransactionContext context) throws Exception;
    }
}