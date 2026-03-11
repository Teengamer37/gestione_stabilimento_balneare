package com.example.s_balneare.application.service.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.User;

public class ManageUserService<T extends User> {
    protected final UserRepository<T> userRepository;
    protected final TransactionManager transactionManager;

    public ManageUserService(UserRepository<T> userRepository, TransactionManager transactionManager) {
        this.userRepository = userRepository;
        this.transactionManager = transactionManager;
    }

    protected void verifyPassword(String rawPassword, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), hashedPassword);
        // Se la password non corrisponde, lanciamo l'eccezione
        if (!result.verified) {
            // Messaggio generico per la sicurezza
            throw new IllegalArgumentException("ERROR: Wrong Password");
        }
    }

    public int logIn(String identifier, String rawPassword, TransactionContext context) {
        String hashedPassword = userRepository.findPassword(identifier, context)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Invalid password or user"));
        // Verifichiamo la password
        verifyPassword(rawPassword, hashedPassword);
        T user = userRepository.findByIdentifier(identifier, context)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Invalid user"));
        return user.getId();
    }

    public void updatePassword(Integer id, String oldPassword, String newPassword) {
        transactionManager.executeInTransaction(context->{
            String hashedPassword = userRepository.findPassword(id, context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: Invalid password or user"));
            verifyPassword(oldPassword, hashedPassword);
            userRepository.updatePassword(id,newPassword, context);
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
