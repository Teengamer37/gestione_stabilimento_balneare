package com.example.s_balneare.application.port.out;

import com.example.s_balneare.domain.user.AppUser;

import java.util.Optional;

//porta (interfaccia) uscente dall'applicazione
//(serve per poi implementare queste funzioni nella classe che userà JDBC e MySQL)
public interface AppUserRepository {
    //salva utente
    int save(AppUser user, String password);
    //cancella utente
    void delete(int id);
    //aggiorna dati utenti
    void update(AppUser user, String password);
    //trova utente tramite un id
    Optional<AppUser> findById(int id);
    //trova utente tramite username
    Optional<AppUser> findByUsername(String username);
    //trova utenti tramite email
    Optional<AppUser> findByEmail(String email);
    //trova utenti tramite numero di telefono
    Optional<AppUser> findByPhoneNumber(String phoneNumber);
}
//(serve per poi implementare queste funzioni nella classe che userà JDBC e MySQL)

