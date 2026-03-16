package com.example.s_balneare.infrastructure.persistence.jdbc.beach;

import com.example.s_balneare.application.port.out.beach.BeachCatalogQuery;
import com.example.s_balneare.application.port.out.beach.BeachSummary;
import com.example.s_balneare.domain.beach.BeachServices;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.infrastructure.persistence.jdbc.common.JdbcTransactionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository che gestisce tramite SQL e JDBC metodi per estrarre determinate collezioni di Beach dal Database.
 *
 * @see BeachCatalogQuery BeachCatalogQuery
 */
public class JdbcBeachCatalogQuery implements BeachCatalogQuery {
    /**
     * METODO HELPER:
     * prende il token vuoto (TransactionContext)
     * e lo converte di nuovo nella classe concreta per estrarre java.sql.Connection.
     *
     * @param context Token connessione
     * @return oggetto java.sql.Connection implementato in JDBC
     * @throws IllegalArgumentException se il token non è di tipo JdbcTransactionContext (quindi non rispecchia il JDBC)
     */
    private Connection getConnection(TransactionContext context) {
        if (!(context instanceof JdbcTransactionManager.JdbcTransactionContext jdbcContext)) {
            throw new IllegalArgumentException("ERROR: context must be of type JdbcTransactionContext");
        }
        return jdbcContext.connection();
    }

    /**
     * Cerca le spiagge salvate nel DB con stato active = TRUE e con nome città o paese uguale al parametro passato.
     *
     * @param keyword Città/Paese da filtrare
     * @param context Connessione JDBC
     * @return lista di spiagge che rispettano i parametri di ricerca
     * @see BeachSummary BeachSummary
     */
    @Override
    public List<BeachSummary> searchActiveBeaches(String keyword, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //se non inserisco nessun parametro di ricerca, ritorno tutte le spiagge attive
        if (keyword == null || keyword.isBlank()) {
            return findAllActiveBeaches(connection);
        }

        //altrimenti, cerco sia per nome della spiaggia che per nome della città
        String sql = buildBaseQuery() + " WHERE b.active = TRUE AND (b.name LIKE ? OR a.city LIKE ?)";

        String searchParam = "%" + keyword.trim() + "%";
        return executeQuery(sql, searchParam, connection);
    }

    // METODI PRIVATI

    /**
     * Cerca tutte le spiagge in stato active = TRUE (chiamato da searchActiveBeaches se keyword = null).
     *
     * @param connection Connessione JDBC
     * @return lista di spiagge attive
     * @see BeachSummary BeachSummary
     * @see #searchActiveBeaches(String, TransactionContext) searchActiveBeaches()
     */
    private List<BeachSummary> findAllActiveBeaches(Connection connection) {
        String sql = buildBaseQuery() + " WHERE b.active = TRUE";
        return executeQuery(sql, null, connection);
    }

    /**
     * Query di base usata da tutti i metodi di questa classe.
     *
     * @return query di base facilmente ampliabile con parametri di ricerca
     * @see #searchActiveBeaches(String, TransactionContext) searchActiveBeaches()
     * @see #findAllActiveBeaches(Connection) findAllActiveBeaches()
     */
    private String buildBaseQuery() {
        return "SELECT b.id AS beach_id, b.name, b.phoneNumber, " +
                "a.id AS address_id, a.street, a.streetNumber, a.city, a.zipCode, a.country, " +
                "bs.bathrooms, bs.showers, bs.pool, bs.bar, bs.restaurant, bs.wifi, bs.volleyballField " +
                "FROM beaches b " +
                "JOIN addresses a ON b.addressId = a.id " +
                "LEFT JOIN beach_services bs ON b.id = bs.beachId";
    }

    /**
     * Metodo privato che va a eseguire la query creata dalle funzioni di questa classe.
     *
     * @param sql        Query SQL
     * @param param      Parametro da mettere nella ricerca (searchActiveBeaches())
     * @param connection Connessione JDBC
     * @return lista di spiagge che rispettano i parametri di ricerca
     * @throws RuntimeException se ci sono problemi di connessione col Database
     * @see #searchActiveBeaches(String, TransactionContext) searchActiveBeaches()
     * @see #findAllActiveBeaches(Connection) findAllActiveBeaches()
     */
    private List<BeachSummary> executeQuery(String sql, String param, Connection connection) {
        List<BeachSummary> results = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            //se ho parametri di ricerca, sostituisco gli eventuali '?' nella query
            if (param != null) {
                ps.setString(1, param);
                ps.setString(2, param);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    //recupero BeachServices
                    BeachServices services = new BeachServices(
                            rs.getBoolean("bathrooms"),
                            rs.getBoolean("showers"),
                            rs.getBoolean("pool"),
                            rs.getBoolean("bar"),
                            rs.getBoolean("restaurant"),
                            rs.getBoolean("wifi"),
                            rs.getBoolean("volleyballField")
                    );

                    //recupero tutti i dettagli necessari lato utente
                    results.add(new BeachSummary(
                            rs.getInt("beach_id"),
                            new Address(
                                    rs.getInt("address_id"),
                                    rs.getString("street"),
                                    rs.getString("streetNumber"),
                                    rs.getString("city"),
                                    rs.getString("zipCode"),
                                    rs.getString("country")
                            ),
                            rs.getString("name"),
                            rs.getString("city"),
                            rs.getString("phoneNumber"),
                            services,
                            rs.getString("extraInfo")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: Unable to query beach catalog", e);
        }
        return results;
    }
}