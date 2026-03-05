package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.AppUserRepository;
import com.example.s_balneare.application.port.out.OwnerUserRepository;
import com.example.s_balneare.domain.user.AppUser;

import java.util.Optional;

//FIXME: un macello qui pt2

public class JdbcOwnerUserRepository implements AppUserRepository, OwnerUserRepository {

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
    public Optional<AppUser> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<AppUser> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<AppUser> findAll() {
        return Optional.empty();
    }
}