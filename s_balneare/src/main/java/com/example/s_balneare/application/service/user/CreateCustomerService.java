package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.user.CreateCustomerRequest;
import com.example.s_balneare.application.port.out.AddressRepository;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.Customer;

public class CreateCustomerService extends CreateUserService<Customer, CreateCustomerRequest> {

    private final AddressRepository addressRepository;

    public CreateCustomerService(UserRepository<Customer> userRepository, AddressRepository addressRepository, TransactionManager transactionManager) {
        super(userRepository, transactionManager);
        this.addressRepository = addressRepository;
    }

    @Override
    protected Customer registerUser(CreateCustomerRequest request, TransactionContext context) {
        Address address = new Address(
                0,
                request.getStreet(),
                request.getStreetNumber(),
                request.getCity(),
                request.getZipCode(),
                request.getCountry()
        );
        Integer addressId = addressRepository.save(address, context);

        return new Customer(
                0,
                request.getEmail(),
                request.getUsername(),
                request.getName(),
                request.getSurname(),
                request.getPhoneNumber(),
                addressId,
                request.isActive()
        );
    }
}