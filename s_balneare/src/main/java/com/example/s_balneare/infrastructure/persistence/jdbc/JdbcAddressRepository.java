package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.common.AddressRepository;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.common.TransactionContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository che implementa tutti i metodi che permettono di interagire con un Database su oggetti di tipo Address tramite
 * libreria JDBC.
 * @see AddressRepository AddressRepository
 * @see com.example.s_balneare.application.port.out.TransactionManager TransactionManager per le transazioni SQL
 */
public class JdbcAddressRepository implements AddressRepository {
    /**
     * METODO HELPER:
     * prende il token vuoto (TransactionContext) e
     * lo converte di nuovo nella classe concreta per estrarre java.sql.Connection
     * @param context Token connessione
     * @return oggetto java.sql.Connection implementato in JDBC
     * @throws IllegalArgumentException se il token non è di tipo JdbcTransactionContext (quindi non rispecchia il JDBC)
     */
    private Connection getConnection(TransactionContext context) {
        if (!(context instanceof JdbcTransactionManager.JdbcTransactionContext jdbcContext)) {
            throw new IllegalArgumentException("ERROR: context must be of type JdbcTransactionContext");
        }
        return jdbcContext.getConnection();
    }

    /**
     * Salvataggio indirizzo nel DB, restituendo ID da associare poi a Beach o User.
     * Usato direttamente dagli use cases di Beach e User, nessun rischio di avere address non associati
     * @param address Indirizzo da salvare nel Database
     * @param context Connessione JDBC
     * @return ID generato dal Database
     * @throws SQLException se ci sono problemi col Database
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public Integer save(Address address, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        String sql = "INSERT INTO addresses (street, streetNumber, city, zipCode, country) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, address.street());
            statement.setString(2, address.streetNumber());
            statement.setString(3, address.city());
            statement.setString(4, address.zipCode());
            statement.setString(5, address.country());
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("ERROR: SQL failed, no ID generated for address");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to save address", e);
        }
    }

    /**
     * Aggiorna indirizzo nel DB
     * @param address Indirizzo da aggiornare
     * @param context Connessione JDBC
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public void update(Address address, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (address.id() == null) throw new IllegalArgumentException("ERROR: address must have a valid ID");

        String sql = "UPDATE addresses SET street = ?, streetNumber = ?, city = ?, zipCode = ?, country = ? WHERE id = ?";

        //apro connessione
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, address.street());
                statement.setString(2, address.streetNumber());
                statement.setString(3, address.city());
                statement.setString(4, address.zipCode());
                statement.setString(5, address.country());
                statement.setInt(6, address.id());
                statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to update address", e);
        }
    }

    /**
     * Cerca indirizzo nel DB per ID
     * @param id ID dell'indirizzo
     * @param context Connessione JDBC
     * @return oggetto Optional dal quale, se trovato l'indirizzo, può essere estratto l'oggetto Address; altri metodi altrimenti
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public Optional<Address> findById(Integer id, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        String sql = "SELECT id, street, streetNumber, city, zipCode, country FROM addresses WHERE id = ?";

        //apro connessione
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Address(
                            rs.getInt("id"),
                            rs.getString("street"),
                            rs.getString("streetNumber"),
                            rs.getString("city"),
                            rs.getString("zipCode"),
                            rs.getString("country")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find address by ID", e);
        }
        return Optional.empty();
    }

    /**
     * Cerca indirizzi nel DB per città
     * @param city Città da cercare
     * @param context Connessione JDBC
     * @return una lista di indirizzi che hanno la stessa città passata come parametro
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public List<Address> findByCity(String city, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità stringa
        if (city == null || city.isBlank()) throw new IllegalArgumentException("ERROR: the parameter is either blank or null");

        String sql = "SELECT id, street, streetNumber, city, zipCode, country FROM addresses WHERE city = ?";
        List<Address> addresses = new ArrayList<>();

        //apro connessione
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, city);

            //ciclo while per scorrere in tutte le righe e salvarle nella lista
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    addresses.add(new Address(rs.getInt("id"), rs.getString("street"), rs.getString("streetNumber"), rs.getString("city"), rs.getString("zipCode"), rs.getString("country")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find address by value", e);
        }

        return addresses;
    }

    /**
     * Cerca indirizzi nel DB per paese
     * @param country Paese da cercare
     * @param context Connessione JDBC
     * @return una lista di indirizzi che hanno lo stesso paese passato come parametro
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public List<Address> findByCountry(String country, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità stringa
        if (country == null || country.isBlank()) throw new IllegalArgumentException("ERROR: the parameter is either blank or null");

        String sql = "SELECT id, street, streetNumber, city, zipCode, country FROM addresses WHERE country = ?";
        List<Address> addresses = new ArrayList<>();

        //apro connessione
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, country);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    addresses.add(new Address(rs.getInt("id"), rs.getString("street"), rs.getString("streetNumber"), rs.getString("city"), rs.getString("zipCode"), rs.getString("country")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find address by value", e);
        }

        return addresses;
    }

    /**
     * Ritorna tutti gli indirizzi presenti nel DB
     * (solo scopo di filtraggio e manipolazione in-app + eventuali esperimenti)
     * @param context Connessione JDBC
     * @return una lista di tutti gli indirizzi salvati nel Database
     */
    @Override
    public List<Address> findAll(TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        String sql = "SELECT id, street, streetNumber, city, zipCode, country FROM addresses";
        List<Address> addresses = new ArrayList<>();

        //apro connessione
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    addresses.add(new Address(rs.getInt("id"), rs.getString("street"), rs.getString("streetNumber"), rs.getString("city"), rs.getString("zipCode"), rs.getString("country")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find addresses", e);
        }

        return addresses;
    }

    /**
     * Elimina un indirizzo dal DB
     * @param id ID dell'indirizzo da eliminare
     * @param context Connessione JDBC
     */
    @Override
    public void delete(Integer id, TransactionContext context) {
        //estraggo la connessione JDBC
        Connection connection = getConnection(context);

        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        String sql = "DELETE FROM addresses WHERE id = ?";

        //apro connessione
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to delete address", e);
        }
    }
}