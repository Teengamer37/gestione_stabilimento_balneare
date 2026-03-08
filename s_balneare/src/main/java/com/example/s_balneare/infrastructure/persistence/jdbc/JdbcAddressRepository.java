package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.AddressRepository;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.common.TransactionContext;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository che implementa tutti i metodi che permettono di interagire con un Database su oggetti di tipo Address tramite
 * libreria JDBC.
 * @see com.example.s_balneare.application.port.out.AddressRepository AddressRepository
 */
public class JdbcAddressRepository implements AddressRepository {
    private final DataSource dataSource;

    public JdbcAddressRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * METODO HELPER:
     * prende il token vuoto (TransactionContext) e
     * lo converte di nuovo nella classe concreta per estrarre java.sql.Connection
     * @param context Token vuoto
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
     * DA USARE SOLO SE SI HA INTENZIONE IN UN FUTURO VICINO DI ESEGUIRE MANUALMENTE UN SALVATAGGIO DI BEACH O USER
     * @param address Indirizzo da salvare nel Database
     * @return ID generato dal Database
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public Integer save(Address address) {
        try (Connection connection = dataSource.getConnection()) {
            TransactionContext context = new JdbcTransactionManager.JdbcTransactionContext(connection);
            return save(address, context);
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to get connection for saving address", e);
        }
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
        //estraggo la connection JDBC
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
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public void update(Address address) {
        //check validità ID
        if (address.id() == null) throw new IllegalArgumentException("ERROR: address must have a valid ID");

        String sql = "UPDATE addresses SET street = ?, streetNumber = ?, city = ?, zipCode = ?, country = ? WHERE id = ?";

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
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
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to update address", e);
        }
    }

    /**
     * Cerca indirizzo nel DB per ID
     * @param id ID dell'indirizzo
     * @return oggetto Optional dal quale, se trovato l'indirizzo, può essere estratto l'oggetto Address; altri metodi altrimenti
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public Optional<Address> findById(Integer id) {
        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        String sql = "SELECT id, street, streetNumber, city, zipCode, country FROM addresses WHERE id = ?";

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
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
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find address by ID", e);
        }
        return Optional.empty();
    }

    /**
     * Cerca indirizzi nel DB per città
     * @param city Città da cercare
     * @return una lista di indirizzi che hanno la stessa città passata come parametro
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public List<Address> findByCity(String city) {
        //check validità stringa
        if (city == null || city.isBlank()) throw new IllegalArgumentException("ERROR: the parameter is either blank or null");

        String sql = "SELECT id, street, streetNumber, city, zipCode, country FROM addresses WHERE city = ?";
        List<Address> addresses = new ArrayList<>();

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
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
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find address by value", e);
        }

        return addresses;
    }

    /**
     * Cerca indirizzi nel DB per paese
     * @param country Paese da cercare
     * @return una lista di indirizzi che hanno lo stesso paese passato come parametro
     * @throws IllegalArgumentException se ci sono parametri non validi
     * @throws RuntimeException se ci sono problemi di connessione col Database
     */
    @Override
    public List<Address> findByCountry(String country) {
        //check validità stringa
        if (country == null || country.isBlank()) throw new IllegalArgumentException("ERROR: the parameter is either blank or null");

        String sql = "SELECT id, street, streetNumber, city, zipCode, country FROM addresses WHERE country = ?";
        List<Address> addresses = new ArrayList<>();

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
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
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find address by value", e);
        }

        return addresses;
    }

    /**
     * Ritorna tutti gli indirizzi presenti nel DB
     * (solo scopo di filtraggio e manipolazione in-app + eventuali esperimenti)
     * @return una lista di tutti gli indirizzi salvati nel Database
     */
    @Override
    public List<Address> findAll() {
        String sql = "SELECT id, street, streetNumber, city, zipCode, country FROM addresses";
        List<Address> addresses = new ArrayList<>();

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        addresses.add(new Address(rs.getInt("id"), rs.getString("street"), rs.getString("streetNumber"), rs.getString("city"), rs.getString("zipCode"), rs.getString("country")));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("ERROR: unable to find addresses", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find addresses", e);
        }

        return addresses;
    }

    /**
     * Elimina un indirizzo dal DB
     * @param id ID dell'indirizzo da eliminare
     */
    @Override
    public void delete(Integer id) {
        //check validità ID
        if (id == null || id <= 0) throw new IllegalArgumentException("ERROR: the parameter is not valid");

        String sql = "DELETE FROM addresses WHERE id = ?";

        //apro connessione
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("ERROR: unable to delete address", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to delete address", e);
        }
    }
}