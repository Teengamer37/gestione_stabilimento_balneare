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
        //passo 1: hash password
        String hashedPassword = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());

        //passo 2: apro transazione
        return transactionManager.executeInTransaction(context -> {

            //passo 3: chiamo funzione registerUser
            T userEntity = registerUser(request, context);

            //passo 4: salvo nel DB
            return userRepository.save(userEntity, hashedPassword, context);
        });
    }
}