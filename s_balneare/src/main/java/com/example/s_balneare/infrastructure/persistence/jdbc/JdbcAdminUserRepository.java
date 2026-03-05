package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.AdminUserRepository;
import com.example.s_balneare.domain.user.AdminUser;
import com.example.s_balneare.domain.user.AppUser;

import java.util.Optional;

//TODO: continuare l'implementazione

public class JdbcAdminUserRepository implements AdminUserRepository {


    @Override
    public Integer save(AdminUser user, String password) {
        return 0;
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public void update(AdminUser user) {

    }

    @Override
    public void updatePassword(AppUser user, String password) {

    }

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
    public Optional<AdminUser> findAll() {
        return Optional.empty();
    }
}
