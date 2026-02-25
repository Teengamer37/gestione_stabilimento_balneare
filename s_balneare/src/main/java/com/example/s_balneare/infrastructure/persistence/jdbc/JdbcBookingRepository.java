package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.BookingRepository;
import com.example.s_balneare.domain.booking.Booking;

import java.sql.*;
import java.util.Optional;

/// TBD: aggiornare TUTTE LE CLASSI in domain package
/// prima di proseguire con le repository in JDBC!!!
/// (ovvero togliere tutte le references e mettere ID al posto loro)
/// (esempio: Beach beach -> int beachId)

public class JdbcBookingRepository implements BookingRepository {
    private final Connection connection;

    public JdbcBookingRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int save(Booking booking) {
        String sql = "INSERT INTO bookings(beach, customer, date, extraSdraio, extraLettini, extraSedie, camerini) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            /// TBD: aggiornare questi valori dopo le modifiche!!!
            statement.setInt(1, booking.getBeach().getId());
            statement.setInt(2, booking.getCustomer().getId());
            statement.setDate(3, java.sql.Date.valueOf(booking.getDate()));
            statement.setInt(4, booking.getExtraSdraio());
            statement.setInt(5, booking.getExtraLettini());
            statement.setInt(6, booking.getExtraSedie());
            statement.setInt(7, booking.getCamerini());
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            } catch (SQLException e) {
                throw new RuntimeException("ERROR: SQL insert not executed correctly", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to save booking", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM bookings WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to delete booking", e);
        }
    }

    @Override
    public void update(Booking booking) {
        String sql = "UPDATE bookings SET extraSdraio = ?, extraLettini = ?, extraSedie = ?, camerini = ?, status = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, booking.getExtraSdraio());
            statement.setInt(2, booking.getExtraLettini());
            statement.setInt(3, booking.getExtraSedie());
            statement.setInt(4, booking.getCamerini());
            statement.setString(5, booking.getStatus().name());
            statement.setInt(6, booking.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to update booking", e);
        }
    }

    ///TBD: rivisitare tutta la query e continuare coi metodi
    @Override
    public Optional<Booking> findById(int id) {
        String sql = "SELECT b.*," +
                "au.name AS customerName, au.surname AS customerSurname, au.username AS customerUsername, au.email AS customerEmail, au.active AS customerActive," +
                "c.telephoneNumber as customerTelephoneNumber" +
                "ad.street AS customerStreet, ad.streetNumber AS customerStreetNumber, ad.city AS customerCity, ad.zipCode AS customerZipCode, ad.country AS customerCountry" +
                "" +
                "FROM bookings b WHERE id = ?" +
                "JOIN customers c ON b.customer = c.id" +
                "JOIN beaches be ON b.beach = be.id" +
                "JOIN app_users au ON au.id = b.customer" +
                "JOIN addresses ad ON ad.id = au.address";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, id);
            statement.executeQuery();

            try (ResultSet rs = statement.getResultSet()) {
                rs.next();

                // TBD: da modificare IL PRIMA POSSIBILE
                return Optional.empty();
                //return new Booking.BookingBuilder(rs.getInt(1))
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: unable to find booking", e);
        }
    }
}