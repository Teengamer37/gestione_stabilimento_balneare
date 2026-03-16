package com.example.s_balneare.application.port.out.user;

import com.example.s_balneare.domain.user.Admin;

/**
 * Interfaccia che gestisce la manipolazione di Admin tra l'app e il Database.<br>
 * Estende:
 *
 * @see UserRepository UserRepository
 */
public interface AdminRepository extends UserRepository<Admin> {}