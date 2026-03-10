package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.user.CreateUserRequest;
import com.example.s_balneare.application.port.in.user.UserRegistrationCase;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.User;
import at.favre.lib.crypto.bcrypt.BCrypt;

public abstract class CreateUserService<T extends User, R extends CreateUserRequest>
    implements UserRegistrationCase<R> {
    private final UserRepository<T> userRepository;
    private final TransactionManager transactionManager;

    public CreateUserService(UserRepository<T> userRepository, TransactionManager transactionManager) {
        this.userRepository = userRepository;
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
            return userRepository.save(userEntity, hashedPassword, context);
        });
    }

}