package com.example.s_balneare.application.port.out.user;

import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.User;

import java.util.List;
import java.util.Optional;

//interfacce per manipolazione oggetti di tipo AppUser
public interface UserRepository<T extends User> {
    Integer save(T user, String password, TransactionContext context);
    void update(T user, TransactionContext context);

    Optional<String> findPassword(Integer id, TransactionContext context);
    Optional<String> findPassword(String identifier, TransactionContext context);
    void updatePassword(Integer id, String password, TransactionContext context);

    Optional<T> findById(Integer id, TransactionContext context);
    Optional<T> findByIdentifier(String identifier, TransactionContext context);
    Optional<T> findByUsername(String username, TransactionContext context);
    Optional<T> findByEmail(String email, TransactionContext context);
    List<T> findAll(TransactionContext context);
}