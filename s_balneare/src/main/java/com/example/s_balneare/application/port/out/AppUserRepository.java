package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.user.AppUser;

import java.util.Optional;

//interfacce per manipolazione oggetti di tipo AppUser
public interface AppUserRepository {
    Integer save(AppUser user, String password);
    void delete(Integer id);
    void update(AppUser user, String password);
    //TODO: scelta implementativa modifica password scegli come vuoi fare:
    //Se scegli di avere due update fai refactor dell'update soprastante eliminando l'attributo password successivamente
    //leva dai commenti il metodo sottostante e spostati in AppUserService e rendi codice il commento
    // TODO: appUserRepository.updatePassword(appUser, password);
    //void updatePassword(AppUser user, String password)
    Optional<AppUser> findById(Integer id);
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findAll();
}

//consiglio: farei 3 interfacce diverse
//CustomerUserRepository, OwnerUserRepository, AdminUserRepository