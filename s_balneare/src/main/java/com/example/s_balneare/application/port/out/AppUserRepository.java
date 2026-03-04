package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.user.AppUser;

import java.util.Optional;

//interfacce per manipolazione oggetti di tipo AppUser
public interface AppUserRepository {
    Integer save(AppUser user, String password);
    void delete(Integer id);
    void update(AppUser user, String password);
    Optional<AppUser> findById(Integer id);
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByPhoneNumber(String phoneNumber);
}

//consiglio: farei 3 interfacce diverse
//CustomerUserRepository, OwnerUserRepository, AdminUserRepository