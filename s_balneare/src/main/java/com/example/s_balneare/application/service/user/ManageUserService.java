package com.example.s_balneare.application.service.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.user.LoginResult;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.Admin;
import com.example.s_balneare.domain.user.Owner;
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
        //se la password non corrisponde, lancio l'eccezione
        if (!result.verified) {
            throw new IllegalArgumentException("ERROR: wrong Password");
        }
    }

    //TODO: spostare funzione in una nuova AuthenticationService!
    //TODO: in fase di login, il sistema deve controllare vari ban: utente bannato, o un Owner con un ban o con una Beach in stato CLOSED
    //TODO: Fare prima BanRepository in modo che possa collaborare con AuthenticationService!!!
    public LoginResult logIn(String identifier, String rawPassword, TransactionContext context) {
        String hashedPassword = userRepository.findPassword(identifier, context)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: invalid password or user"));
        //verifico la password
        verifyPassword(rawPassword, hashedPassword);
        T user = userRepository.findByIdentifier(identifier, context)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: invalid user"));
        return new LoginResult(user.getId(), user.isOTP());
    }

    //FIXME: fare funzione updateOTP? (consigliato per non fare un findById e poi un update, anche se sticazzi)
    public void updatePassword(Integer id, String oldPassword, String newPassword, boolean OTP) {
        transactionManager.executeInTransaction(context -> {
            //cerco password vecchia
            String oldHashedPassword = userRepository.findPassword(id, context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: invalid password or user"));

            //verifico password vecchia
            verifyPassword(oldPassword, oldHashedPassword);

            //hash nuova password
            String newHashedPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray());

            //aggiorno password nel DB
            userRepository.updatePassword(id, newHashedPassword, context);

            //caso OTP: recupero user, disattivo il flag, aggiorno nel DB
            if (OTP) {
                T user = getUserOrThrow(id, context);
                if (user instanceof Owner owner) {
                    owner.updateOTP(false);
                } else if (user instanceof Admin admin) {
                    admin.updateOTP(false);
                }
                userRepository.update(user, context);
            }
        });
    }

    //transazione unica, aggiornamento indirizzo sarà gestito da un'altra transazione ed interfaccia
    public void updateDatas(Integer id, String name, String surname, String username) {
        transactionManager.executeInTransaction(context -> {
            T user = getUserOrThrow(id, context);
            user.updateName(name);
            user.updateSurname(surname);
            user.updateUsername(username);
            userRepository.update(user, context);
        });
    }
    //transazione unica eliminazione email, da gestire per controlli lato applicazione
    public void updateEmail (Integer id, String email) {
        transactionManager.executeInTransaction(context -> {
            T user = getUserOrThrow(id, context);
            user.updateEmail(email);
            userRepository.update(user, context);
        });
    }

    //TODO:controlla passaggio context in find ERRORE: creazione due connessioni (SOLDI) probabilemente eliminabile o spostabile
    protected T getUserOrThrow(Integer id, TransactionContext context){
        return userRepository.findById(id,context)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: user not found with id: " + id));
    }
}