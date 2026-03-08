package com.example.s_balneare.infrastructure.persistence.jdbc;

import com.example.s_balneare.application.port.out.OwnerUserRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.OwnerUser;

import java.util.List;
import java.util.Optional;

//FIXME: un macello qui pt2

public class JdbcOwnerUserRepository implements OwnerUserRepository {

    @Override
    public Integer save(OwnerUser user, String password, TransactionContext context) {
        return 0;
    }

    @Override
    public void delete(Integer id, TransactionContext context) {

    }

    @Override
    public void update(OwnerUser user, TransactionContext context) {

    }

    @Override
    public void updatePassword(AppUser user, String password, TransactionContext context) {

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
    public List<OwnerUser> findAll() {
        return Optional.empty();
    }
}