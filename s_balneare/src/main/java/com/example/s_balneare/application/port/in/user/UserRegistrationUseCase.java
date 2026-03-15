package com.example.s_balneare.application.port.in.user;

/**
 * Interfaccia che definisce il metodo di registrazione di un nuovo User.
 *
 * @see CreateUserRequest CreateUserRequest
 */
public interface UserRegistrationUseCase<R extends CreateUserRequest> {
    int register(R request, String rawPassword);
}