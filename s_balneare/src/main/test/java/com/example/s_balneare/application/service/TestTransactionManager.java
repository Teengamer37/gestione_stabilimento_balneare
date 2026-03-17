package com.example.s_balneare.application.service;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;

/**
 * Un TransactionManager fittizio per i test unitari.
 * Esegue il blocco di codice immediatamente senza aprire connessioni JDBC.
 *
 * @see TransactionManager TransactionManager
 */
public class TestTransactionManager implements TransactionManager {
    private final TransactionContext dummyContext = new TransactionContext() {};

    @Override
    public <T> T executeInTransaction(TransactionCallable<T> callable) {
        try {
            //eseguo direttamente la lambda passata dal service, iniettando il contesto fittizio
            return callable.execute(dummyContext);
        } catch (Exception e) {
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeInTransaction(TransactionRunnable runnable) {
        try {
            //eseguo direttamente la lambda passata dal service, iniettando il contesto fittizio
            runnable.execute(dummyContext);
        } catch (Exception e) {
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException(e);
        }
    }
}