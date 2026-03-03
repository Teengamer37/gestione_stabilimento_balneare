package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.AppUserRepository;
import com.example.s_balneare.domain.user.AppUser;

import java.util.Optional;

//TODO: continuare l'implementazione

public class JdbcAdminUserRepository implements AppUserRepository {

    @Override
    public int save(AppUser user, String password) {
        return 0;
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void update(AppUser user, String password) {

    }

    @Override
    public Optional<AppUser> findById(int id) {
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
    public Optional<AppUser> findByPhoneNumber(String phoneNumber) {
        return Optional.empty();
    }
}
