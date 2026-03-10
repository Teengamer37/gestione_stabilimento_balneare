package com.example.s_balneare.application.port.out.user;

import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.Customer;

import java.util.Optional;

public interface CustomerRepository extends UserRepository<Customer> {
    Optional<Customer> findByPhoneNumber(String phoneNumber, TransactionContext context);
}