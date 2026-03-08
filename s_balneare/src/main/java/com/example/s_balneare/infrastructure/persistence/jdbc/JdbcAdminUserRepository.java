package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.AdminUserRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.AdminUser;
import com.example.s_balneare.domain.user.AppUser;

import java.util.List;
import java.util.Optional;

//TODO: continuare l'implementazione

public class JdbcAdminUserRepository implements AdminUserRepository {


    @Override
    public Integer save(AdminUser user, String password, TransactionContext context) {
        return 0;
    }

    @Override
    public void delete(Integer id, TransactionContext context) {

    }

    @Override
    public void update(AdminUser user, TransactionContext context) {

    }

    @Override
    public void updatePassword(AppUser user, String password, TransactionContext context) {

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
    public List<AdminUser> findAll() {
        return Optional.empty();
    }
}
