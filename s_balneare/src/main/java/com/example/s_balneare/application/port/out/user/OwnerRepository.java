package com.example.s_balneare.application.port.out.user;

import com.example.s_balneare.domain.user.Owner;

/**
 * Interfaccia che gestisce la manipolazione di Owner tra l'app e il Database.
 * <p>Estende:
 *
 * @see UserRepository UserRepository
 */
public interface OwnerRepository extends UserRepository<Owner> {}