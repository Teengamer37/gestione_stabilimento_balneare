package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.user.CustomerUserRequest;
import com.example.s_balneare.application.port.out.AddressRepository;
import com.example.s_balneare.application.port.out.user.AppUserRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.CustomerUser;

public class CustomerUserService extends AppUserService<CustomerUser, CustomerUserRequest>  {

    private final AddressRepository addressRepository;


    public CustomerUserService(AppUserRepository<CustomerUser> appUserRepository, AddressRepository addressRepository, TransactionManager transactionManager) {
        super(appUserRepository, transactionManager);
        this.addressRepository = addressRepository;
    }

    @Override
    protected CustomerUser registerUser(CustomerUserRequest request, TransactionContext context) {
        Address address = new Address(0,request.getStreet(),request.getStreetNumber(),request.getCity(),request.getZipCode(),request.getCountry());
        Integer addressId = addressRepository.save(address, context);
        return new CustomerUser(
                0, request.getEmail(), request.getUsername(), request.getName(), request.getSurname(), request.getPhoneNumber(), addressId, request.isActive()
        );
    }

}