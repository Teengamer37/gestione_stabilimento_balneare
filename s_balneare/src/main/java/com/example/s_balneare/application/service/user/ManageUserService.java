package com.example.s_balneare.application.service.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.s_balneare.application.port.out.TransactionManager;
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
    public void updateEmail (Integer id, String email, String currentPassword) {
        transactionManager.executeInTransaction(context -> {
            T user = getUserOrThrow(id, context);

            String currentHashedPassword = userRepository.findPassword(id, context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: Password not found"));
            verifyPassword(currentPassword, currentHashedPassword);

            user.updateEmail(email);
            userRepository.update(user, context);
        });
    }

    protected T getUserOrThrow(Integer id, TransactionContext context){
        return userRepository.findById(id,context)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: user not found with id: " + id));
    }
}

//TODO: finire controlla aggiunta metodi con gli useCase