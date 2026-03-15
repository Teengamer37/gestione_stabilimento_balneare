package com.example.s_balneare.application.port.out.user;

public interface AuthenticationUseCase {
    LoginResult logIn(String identifier, String rawPassword);
}