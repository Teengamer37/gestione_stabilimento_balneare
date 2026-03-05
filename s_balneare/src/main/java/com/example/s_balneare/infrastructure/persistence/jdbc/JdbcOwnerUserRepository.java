package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.OwnerUserRepository;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.OwnerUser;

import java.util.Optional;

//FIXME: un macello qui pt2

public class JdbcOwnerUserRepository implements OwnerUserRepository {

    @Override
    public Integer save(AppUser user, String password) {
        return 0;
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public void update(AppUser user) {

    }

    @Override
    public void updatePassword(AppUser user, String password) {

    }

    @Override
    public Optional findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional findAll() {
        return Optional.empty();
    }
}