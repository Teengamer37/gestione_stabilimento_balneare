package com.example.s_balneare.application.service.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.User;

import java.sql.Connection;

public class ManageUserService<T extends User> {
    protected final UserRepository<T> userRepository;
    protected final TransactionManager transactionManager;

    public ManageUserService(UserRepository<T> userRepository, TransactionManager transactionManager) {
        this.userRepository = userRepository;
        this.transactionManager = transactionManager;
    }

    //L'utente può volersi loggare sia con Username che con Email entrambi identificativi, il numero di ti telefono è un attributo accessorio
    public void checkPassword(String idString, String password, TransactionContext context) {

    }

    public void updatePassword (Integer id, String oldPassword, String newPassword) {
        transactionManager.executeInTransaction(context->{
            T user = getUserOrThrow(id, context);
            // CONFRONTO
            /**
            BCrypt.Result result = BCrypt.verifyer().verify(oldPassword.toCharArray(), hashDalDatabase);

            if (result.verified) {
                System.out.println("La password è corretta!");
            } else {
                System.out.println("Password errata.");
            }
             **/
        });

    }
    //Transazione unica, aggiornamento indirizzo sarà gestito da un'altra transazione  ed interfaccia
    public void updateDatas(Integer id,String nome, String Surname, String Username){
        transactionManager.executeInTransaction(context->{
            T user = getUserOrThrow(id, context);
            user.updateName(nome);
            user.updateSurname(Surname);
            user.updateUsername(Username);
            userRepository.update(user, context);
        });
    }
    //Transazione unica eliminazione email, da gestire per controlli lato applicazione
    public void updateEmail (Integer id, String email) {
        transactionManager.executeInTransaction(context->{
            T user = getUserOrThrow(id, context);
            user.updateEmail(email);
            userRepository.update(user, context);
        });
    }

    //TODO:controlla passaggio context in find ERRORE: creazione due connessioni (SOLDI) probabilemente eliminabile o spostabile
    protected T getUserOrThrow(Integer id, TransactionContext context){
        return userRepository.findById(id,context)
                .orElseThrow(()->new IllegalArgumentException("ERROR: User not found with id: " + id));
    }
}
