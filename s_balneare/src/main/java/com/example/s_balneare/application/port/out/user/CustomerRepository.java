package com.example.s_balneare.application.port.out.user;

import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.Customer;

import java.util.Optional;

/**
 * Interfaccia che gestisce la manipolazione di Customer tra l'app e il Database.
 * Estende:
 *
 * @see UserRepository UserRepository
 */
public interface CustomerRepository extends UserRepository<Customer> {
    //ricerca tramite numero di telefono
    Optional<Customer> findByPhoneNumber(String phoneNumber, TransactionContext context);
}