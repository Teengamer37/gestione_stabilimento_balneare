package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.user.AppUserRequest;
import com.example.s_balneare.application.port.in.user.UserRegistrationCase;
import com.example.s_balneare.application.port.out.user.AppUserRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.AppUser;
import at.favre.lib.crypto.bcrypt.BCrypt;

public abstract class AppUserService<T extends AppUser, R extends AppUserRequest>
    implements UserRegistrationCase<R> {
    protected final AppUserRepository<T> appUserRepository;
    protected final TransactionManager transactionManager;

    public AppUserService(AppUserRepository<T> appUserRepository, TransactionManager transactionManager) {
        this.appUserRepository = appUserRepository;
        this.transactionManager = transactionManager;
    }

    protected abstract T registerUser(R request, TransactionContext context);

    @Override
    public final int register(R request, String rawPassword) {
        // 1. Hash FUORI dalla transazione (Performance CPU)
        String hashedPassword = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());

        // 2. Apertura transazione (Atomicità)
        return transactionManager.executeInTransaction(context -> {

            // 3. Chiamata al pezzo mancante (implementato dal figlio)
            T userEntity = registerUser(request, context);

            // 4. Salvataggio finale (tabella app_users + tabelle specifiche)
            return appUserRepository.save(userEntity, hashedPassword, context);
        });
    }


    //TODO:controlla passaggio context in find ERRORE: creazione due connessioni (SOLDI) probabilemente eliminabile o spostabile
    protected T getUserOrThrow(Integer id){
        return appUserRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("ERROR: User not found with id: " + id));
    }
}