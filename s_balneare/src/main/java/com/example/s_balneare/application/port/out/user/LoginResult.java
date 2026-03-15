package com.example.s_balneare.application.port.out.user;

import com.example.s_balneare.domain.user.Role;

/**
 * Record che fornisce all'applicazione dati utili per mantenere una sessione utente-applicazione
 * <p>Usata in:
 *
 * @see AuthenticationUseCase AuthenticationUseCase
 */
public record LoginResult(
        Integer userId,
        boolean requiresPasswordChange,
        Role userRole) {}