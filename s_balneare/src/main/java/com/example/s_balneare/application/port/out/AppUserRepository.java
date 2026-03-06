package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.AppUser;

import java.util.Optional;

//interfacce per manipolazione oggetti di tipo AppUser
public interface AppUserRepository <T extends AppUser>{
    Integer save(T user, String password, TransactionContext context);
    void delete(Integer id, TransactionContext context);
    void update(T user);
    void updatePassword(AppUser user, String password);
    Optional<T> findById(Integer id);
    Optional<T> findByUsername(String username);
    Optional<T> findByEmail(String email);
    Optional<T> findAll();
}
