package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * Repository che implementa tutti i metodi che permettono di semplificare l'interazione tra i Service e il DB.
 * Permette di avviare una SQL Transaction con la libreria JDBC.
 * @see com.example.s_balneare.application.port.out.TransactionManager TransactionManager
 */
public class JdbcTransactionManager implements TransactionManager {
    private final DataSource dataSource;

    public JdbcTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //implementazione REALE del token vuoto
    //nasconde la Connection al resto dell'applicazione
    public static class JdbcTransactionContext implements TransactionContext {
        private final Connection connection;
        public JdbcTransactionContext(Connection connection) {
            this.connection = connection;
        }
        public Connection getConnection() {
            return connection;
        }
    }

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
}