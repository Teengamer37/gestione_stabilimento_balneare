package com.example.s_balneare.infrastructure.persistence.jdbc.user;

import com.example.s_balneare.application.port.out.user.OwnerRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.Owner;
import com.example.s_balneare.domain.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository che implementa i metodi specifici che permettono di interagire con un Database su oggetti di tipo Owner tramite
 * libreria JDBC.<br>
 * Estende JdbcUserRepository.
 *
 * @see JdbcUserRepository JdbcUserRepository
 * @see OwnerRepository OwnerRepository
 */
public class JdbcOwnerRepository extends JdbcUserRepository<Owner> implements OwnerRepository {
    /**
     * Salva dati specifici di un nuovo Owner.<br>
     * Usata assieme al metodo save().
     *
     * @param conn  Connessione JDBC
     * @param newId Nuovo ID ricavato dal salvataggio dei dati generali nel DB
     * @param user  Oggetto Owner
     * @throws SQLException se ci sono problemi col Database
     * @see JdbcUserRepository#save(User, String, TransactionContext) JdbcUserRepository.save()
     */
    @Override
    protected void saveSpecificData(Connection conn, Integer newId, Owner user) throws SQLException {
        //scrivere qui il salvataggio di attributi aggiuntivi di Owner
        String sql = "INSERT INTO owners(id, active, OTP) VALUES(?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, newId);
            statement.setBoolean(2, user.isActive());
            statement.setBoolean(3, user.isOTP());
            statement.executeUpdate();
        }
    }

    /**
     * Aggiorna dati specifici di un Owner.<br>
     * Usata assieme al metodo update().
     *
     * @param conn Connessione JDBC
     * @param user Oggetto Owner
     * @throws SQLException se ci sono problemi col Database
     * @see JdbcUserRepository#update(User, TransactionContext) JdbcUserRepository.update()
     */
    @Override
    protected void updateSpecificData(Connection conn, Owner user) throws SQLException {
        //scrivere qui l'aggiornamento di attributi aggiuntivi di Owner
        String sql = "UPDATE owners SET active = ?, OTP = ?  WHERE id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setBoolean(1, user.isActive());
            statement.setBoolean(2, user.isOTP());
            statement.setInt(3, user.getId());
            statement.executeUpdate();
        }
    }

    /**
     * Cerca un Owner tramite ID nel Database.<br>
     * Usa executeFindQuery per l'avvio della query.
     *
     * @param id      ID dell'Owner da cercare
     * @param context Connessione JDBC
     * @return oggetto Optional da dove, se Owner trovato, si può estrarre l'Owner
     * @see JdbcUserRepository#executeFindQuery(String, TransactionContext, Object...) executeFindQuery()
     */
    @Override
    public Optional<Owner> findById(Integer id, TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, o.active, o.OTP " +
                "FROM users u " +
                "INNER JOIN owners o ON u.id = o.id " +
                "WHERE u.id = ?";

        return executeFindQuery(sql, context, id).stream().findFirst();
    }

    /**
     * Cerca un Owner tramite username o email nel Database.<br>
     * Usa executeFindQuery per l'avvio della query.
     *
     * @param identifier Username/email dell'Owner da cercare
     * @param context    Connessione JDBC
     * @return oggetto Optional da dove, se Owner trovato, si può estrarre l'Owner
     * @see JdbcUserRepository#executeFindQuery(String, TransactionContext, Object...) executeFindQuery()
     */
    @Override
    public Optional<Owner> findByIdentifier(String identifier, TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, o.active, o.OTP " +
                "FROM users u " +
                "INNER JOIN owners o ON u.id = o.id " +
                "WHERE u.username = ? OR u.email = ?";

        //passo identifier due volte (una per lo username, una per l'email)
        return executeFindQuery(sql, context, identifier, identifier).stream().findFirst();
    }

    /**
     * Estrae tutti gli Owner registrati nel DB (da usare solo per scopi di debugging).<br>
     * Usa executeFindQuery per l'avvio della query.
     *
     * @param context Connessione JDBC
     * @return una lista di tutti gli Owner salvati nel Database
     * @see JdbcUserRepository#executeFindQuery(String, TransactionContext, Object...) executeFindQuery()
     */
    @Override
    public List<Owner> findAll(TransactionContext context) {
        String sql = "SELECT u.id, u.name, u.surname, u.username, u.email, o.active, o.OTP " +
                "FROM users u " +
                "INNER JOIN owners o ON u.id = o.id ";

        return executeFindQuery(sql, context);
    }

    /**
     * Metodo che prende l'oggetto ResultSet e restituisce un oggetto Owner creato da esso.
     *
     * @param rs Oggetto contenente riga di una operazione SQL
     * @return oggetto Owner creato
     * @throws SQLException se ci sono problemi col Database
     * @see ResultSet ResultSet
     */
    @Override
    protected Owner mapToEntity(ResultSet rs) throws SQLException {
        return new Owner(rs.getInt("id"),
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getBoolean("active"),
                rs.getBoolean("OTP")
        );
    }
}