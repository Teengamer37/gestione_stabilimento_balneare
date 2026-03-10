package com.example.s_balneare.application.factory;

import com.example.s_balneare.application.port.in.RegistrationRequest;
import com.example.s_balneare.domain.user.Customer;

public class CustomerFactory extends UserFactory {
    @Override
    public Customer createUser(RegistrationRequest request) {
        return new Customer(
                request.getId(),
                request.getEmail(),
                request.getUsername(),
                request.getName(),
                request.getSurname(),
                request.getPhoneNumber(),
                request.getAddressId(),
                request.isActive()
        );
    }
}