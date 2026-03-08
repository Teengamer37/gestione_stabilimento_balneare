package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.AdminUserRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.AdminUser;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.CustomerUser;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

//TODO: continuare l'implementazione

public class JdbcAdminUserRepository
        extends JdbcAppUserRepository<AdminUser>
        implements AdminUserRepository {


    protected JdbcAdminUserRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void saveSpecificData(Connection conn, Integer newId, AdminUser user) throws SQLException {
        //Scrivere qui il salvataggio di attributi aggiuntivi di Admin
    }

    @Override
    protected void deleteSpecificData(Connection conn, Integer newId) throws SQLException {
        //Scrivere qui l'eliminazione di attributi aggiuntivi di Admin
    }

    @Override
    protected void updateSpecificData(Connection conn, AdminUser user) throws SQLException {
        //Scrivere qui l'aggiornamento di attributi aggiuntivi di Admin
    }

    //TODO: scrivere query e controlli nei find
    @Override
    public Optional<AdminUser> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<AdminUser> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<AdminUser> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<AdminUser> findAll() {
        return List.of();
    }

    //TODO: uniformità costruzione user
    @Override
    protected AdminUser mapToEntity(ResultSet rs) throws SQLException {
        //Scrivere qui il codice per mappare gli attributi aggiuntivi di Admin
        return null;
    }
}
