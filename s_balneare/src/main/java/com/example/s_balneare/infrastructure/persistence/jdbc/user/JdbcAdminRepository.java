package com.example.s_balneare.infrastructure.persistence.jdbc.user;

import com.example.s_balneare.application.port.out.user.AdminRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.Admin;
import com.example.s_balneare.domain.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository che implementa i metodi specifici che permettono di interagire con un Database su oggetti di tipo Admin tramite
 * libreria JDBC.<br>
 * Estende JdbcUserRepository.
 *
 * @see JdbcUserRepository JdbcUserRepository
 * @see AdminRepository AdminRepository
 */
public class JdbcAdminRepository extends JdbcUserRepository<Admin> implements AdminRepository {
    /**
     * Salva dati specifici di un nuovo Admin.<br>
     * Usata assieme al metodo save().
     *
     * @param conn  Connessione JDBC
     * @param newId Nuovo ID ricavato dal salvataggio dei dati generali nel DB
     * @param user  Oggetto Admin
     * @throws SQLException se ci sono problemi col Database
     * @see JdbcUserRepository#save(User, String, TransactionContext) JdbcUserRepository.save()
     */
    @Override
    protected void saveSpecificData(Connection conn, Integer newId, Admin user) throws SQLException {
        //scrivere qui il salvataggio di attributi aggiuntivi di Admin
        String sql = "INSERT INTO admins(id, OTP) VALUES(?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, newId);
            statement.setBoolean(2, user.isOTP());
            statement.executeUpdate();
        }
    }

    /**
     * Aggiorna dati specifici di un Admin.<br>
     * Usata assieme al metodo update().
     *
     * @param conn Connessione JDBC
     * @param user Oggetto Admin
     * @throws SQLException se ci sono problemi col Database
     * @see JdbcUserRepository#update(User, TransactionContext) JdbcUserRepository.update()
     */
    @Override
    protected void updateSpecificData(Connection conn, Admin user) throws SQLException {
        //scrivere qui l'aggiornamento di attributi aggiuntivi di Admin
        String sql = "UPDATE admins SET OTP = ? WHERE id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setBoolean(1, user.isOTP());
            statement.setInt(2, user.getId());
            statement.executeUpdate();
        }
    }

    /**
     * Cerca un Admin tramite ID nel Database.<br>
     * Usa executeFindQuery per l'avvio della query.
     *
     * @param id      ID dell'Admin da cercare
     * @param context Connessione JDBC
     * @return oggetto Optional da dove, se Admin trovato, si può estrarre l'Admin
     * @see JdbcUserRepository#executeFindQuery(String, TransactionContext, Object...) executeFindQuery()
     */
    @Override
    public Optional<Admin> findById(Integer id, TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, a.OTP " +
                "FROM users u " +
                "INNER JOIN admins a ON u.id = a.id " +
                "WHERE u.id = ?";

        return executeFindQuery(sql, context, id).stream().findFirst();
    }

    /**
     * Cerca un Admin tramite username o email nel Database.<br>
     * Usa executeFindQuery per l'avvio della query.
     *
     * @param identifier Username/email dell'Admin da cercare
     * @param context    Connessione JDBC
     * @return oggetto Optional da dove, se Admin trovato, si può estrarre l'Admin
     * @see JdbcUserRepository#executeFindQuery(String, TransactionContext, Object...) executeFindQuery()
     */
    public Optional<Admin> findByIdentifier(String identifier, TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, a.OTP " +
                "FROM users u " +
                "INNER JOIN admins a ON u.id = a.id " +
                "WHERE u.username = ? OR u.email = ?";

        //passo identifier due volte (una per lo username, una per l'email)
        return executeFindQuery(sql, context, identifier, identifier).stream().findFirst();
    }

    /**
     * Estrae tutti gli Admin registrati nel DB (da usare solo per scopi di debugging).<br>
     * Usa executeFindQuery per l'avvio della query.
     *
     * @param context Connessione JDBC
     * @return una lista di tutti gli Admin salvati nel Database
     * @see JdbcUserRepository#executeFindQuery(String, TransactionContext, Object...) executeFindQuery()
     */
    @Override
    public List<Admin> findAll(TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, a.OTP " +
                "FROM users u " +
                "INNER JOIN admins a ON u.id = a.id ";
        return executeFindQuery(sql, context);
    }

    /**
     * Metodo che prende l'oggetto ResultSet e restituisce un oggetto Admin creato da esso.
     *
     * @param rs Oggetto contenente riga di una operazione SQL
     * @return oggetto Admin creato
     * @throws SQLException se ci sono problemi col Database
     * @see ResultSet ResultSet
     */
    @Override
    protected Admin mapToEntity(ResultSet rs) throws SQLException {
        //Scrivere qui il codice per mappare gli attributi aggiuntivi di Admin
        return new Admin(rs.getInt("id"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getBoolean("OTP")
        );
    }
}