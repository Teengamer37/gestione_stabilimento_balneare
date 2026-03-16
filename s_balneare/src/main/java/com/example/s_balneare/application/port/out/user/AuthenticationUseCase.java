package com.example.s_balneare.application.port.out.user;

import com.example.s_balneare.application.service.user.AuthenticationService;

/**
 * Interfaccia che definisce metodi per l'autenticazione di un utente nella piattaforma (eseguendo vari controlli).<br>
 * Implementata in:
 *
 * @see AuthenticationService JdbcAuthenticationUseCase
 */
public interface AuthenticationUseCase {
    LoginResult login(String identifier, String rawPassword);
}