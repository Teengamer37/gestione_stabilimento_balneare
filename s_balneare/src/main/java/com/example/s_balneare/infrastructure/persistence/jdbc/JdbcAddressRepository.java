package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.AddressRepository;
import com.example.s_balneare.domain.common.Address;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcAddressRepository implements AddressRepository {
    private final DataSource dataSource;

    public JdbcAddressRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //salvataggio indirizzo nel DB, restituendo ID da associare poi a Beach o User
    //DA USARE SOLO SE SI HA INTENZIONE IN UN FUTURO VICINO DI ESEGUIRE MANUALMENTE UN SALVATAGGIO DI BEACH O USER
    @Override
    public int save(Address address) {
        try (Connection connection = dataSource.getConnection()) {
            return save(address, connection);
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to get connection for saving address", e);
        }
    }

    //salvataggio indirizzo nel DB, restituendo ID da associare poi a Beach o User
    //usato direttamente dagli use cases di Beach e User, nessun rischio di avere address non associati
    @Override
    public int save(Address address, Connection connection) {
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

    //aggiorna indirizzo nel DB
    @Override
    public void update(Address address) {
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

    //cerca indirizzo nel DB per ID
    @Override
    public Optional<Address> findById(int id) {
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

    //cerca indirizzi nel DB per città
    @Override
    public List<Address> findByCity(String city) {
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

    //cerca indirizzi nel DB per paese
    @Override
    public List<Address> findByCountry(String country) {
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

    //ritorna tutti gli indirizzi presenti nel DB
    //(solo scopo di filtraggio e manipolazione in-app + eventuali esperimenti)
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

    //elimina un indirizzo dal DB
    @Override
    public void delete(int id) {
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