package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.user.AppUser;

import java.util.Optional;

//porta (interfaccia) uscente dall'applicazione
//(serve per poi implementare queste funzioni nella classe che userà JDBC e MySQL)
public interface AppUserRepository {
    //salva utente
    int save(AppUser user);
    //cancella utente
    void delete(int id);
    //trova utente tramite un qualsiasi attributo
    Optional<AppUser> find(Object id);
}
//(serve per poi implementare queste funzioni nella classe che userà JDBC e MySQL)

