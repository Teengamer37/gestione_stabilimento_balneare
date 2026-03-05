package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.OwnerUserRepository;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.OwnerUser;

import java.sql.Connection;
import java.util.Optional;

//FIXME: un macello qui pt2

public class JdbcOwnerUserRepository implements OwnerUserRepository {

    @Override
    public Integer save(OwnerUser user, String password, Connection conn) {
        return 0;
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public void update(OwnerUser user) {

    }

    @Override
    public void updatePassword(AppUser user, String password) {

    }

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
    public Optional<OwnerUser> findAll() {
        return Optional.empty();
    }
}