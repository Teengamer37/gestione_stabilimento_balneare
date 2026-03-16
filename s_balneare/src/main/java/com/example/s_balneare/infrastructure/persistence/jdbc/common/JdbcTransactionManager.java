package com.example.s_balneare.infrastructure.persistence.jdbc.common;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * Repository che implementa tutti i metodi che permettono di semplificare l'interazione tra i Service e il DB.<br>
 * Permette di avviare una SQL Transaction con la libreria JDBC.
 *
 * @see TransactionManager TransactionManager
 */
public class JdbcTransactionManager implements TransactionManager {
    private final DataSource dataSource;

    public JdbcTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Esegue una serie di operazioni che restituiscono alla fine un risultato all'interno di una transazione SQL.
     *
     * @param callable La logica da eseguire che restituisce un valore
     * @param <T>      Il tipo del risultato restituito
     * @return il risultato dell'operazione eseguita
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public <T> T executeInTransaction(TransactionCallable<T> callable) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            //impacchetto la connessione nel token
            JdbcTransactionContext context = new JdbcTransactionContext(connection);

            try {
                //eseguo la logica passata come parametro
                T result = callable.execute(context);
                connection.commit();
                return result;
            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException("ERROR: transaction failed, rolled back", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: database connection error", e);
        }
    }

    /**
     * Esegue una serie di operazioni senza valore di ritorno all'interno di una transazione SQL.
     *
     * @param runnable La logica da eseguire
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public void executeInTransaction(TransactionRunnable runnable) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            //impacchetto la connessione nel token
            JdbcTransactionContext context = new JdbcTransactionContext(connection);

            try {
                //eseguo la logica passata come parametro
                runnable.execute(context);
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw new RuntimeException("ERROR: transaction failed, rolled back", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: database connection error", e);
        }
    }

    //implementazione REALE del token vuoto
    //nasconde la Connection al resto dell'applicazione
    public record JdbcTransactionContext(Connection connection) implements TransactionContext {
    }
}