package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.OwnerUserRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.OwnerUser;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

//FIXME: un macello qui pt2

public class JdbcOwnerUserRepository
        extends JdbcAppUserRepository<OwnerUser>
        implements OwnerUserRepository {


    protected JdbcOwnerUserRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void saveSpecificData(Connection conn, Integer newId, OwnerUser user) throws SQLException {
        //Scrivere qui il salvataggio di attributi aggiuntivi di Admin
    }

    @Override
    protected void deleteSpecificData(Connection conn, Integer newId) throws SQLException {
        //Scrivere qui l'eliminazione di attributi aggiuntivi di Admin
    }

    @Override
    protected void updateSpecificData(Connection conn, OwnerUser user) throws SQLException {
        //Scrivere qui l'aggiornamento di attributi aggiuntivi di Admin
    }

    //TODO: scrivere query e controlli metodo find
    @Override
    public Optional<OwnerUser> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<OwnerUser> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<OwnerUser> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public List<OwnerUser> findAll() {
        return List.of();
    }

    //TODO: uniformità costruzione user
    @Override
    protected OwnerUser mapToEntity(ResultSet rs) throws SQLException {
        return null;
    }

}