package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.AddressRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.user.Customer;

public class ManageCustomerService extends ManageUserService<Customer> {
    private final AddressRepository addressRepository;

    public ManageCustomerService(UserRepository<Customer> userRepository, AddressRepository addressRepository, TransactionManager transactionManager) {
        super(userRepository, transactionManager);
        this.addressRepository = addressRepository;
    }

    //cambio numero di telefono transazione unica, gestire lato applicazione eventuali messaggi di conferma
    public void updatePhoneNumber(Integer id, String phoneNumber, String currentPassword) {
        transactionManager.executeInTransaction(context -> {
            Customer user = getUserOrThrow(id, context);

            String currentHashedPassword =userRepository.findPassword(id, context)
                            .orElseThrow(() -> new IllegalArgumentException("ERROR: Password not found"));
            verifyPassword(currentPassword, currentHashedPassword);

            user.changePhoneNumber(phoneNumber);
            userRepository.update(user, context);
        });
    }

    //cambio indirizzo transazione unica, gestire lato applicazione eventuali messaggi di conferma
    public void updateCustomerDatas(Integer id, String name, String surname, String username, Address newAddressData) {
        transactionManager.executeInTransaction(context -> {
            //passo 1: aggiorno dati utente
            Customer customer = getUserOrThrow(id, context);
            customer.updateName(name);
            customer.updateSurname(surname);
            customer.updateUsername(username);

            //passo 2: aggiorno indirizzo associato al Customer
            Address currentAddress = addressRepository.findById(customer.getAddressId(), context)
                    .orElseThrow(() -> new IllegalArgumentException("Address not found"));

            Address updatedAddress = currentAddress
                    .withStreet(newAddressData.street())
                    .withStreetNumber(newAddressData.streetNumber())
                    .withCity(newAddressData.city())
                    .withZipCode(newAddressData.zipCode())
                    .withCountry(newAddressData.country());

            //passo 3: aggiorno tutto nel DB
            addressRepository.update(updatedAddress, context);
            userRepository.update(customer, context);
        });
    }

    //transazione unica disattivazione utente che può essere effettuato sia dall'utente che dall'admin
    public void setCustomerActive(Integer id, boolean active) {
        transactionManager.executeInTransaction(context -> {
            Customer user = getUserOrThrow(id, context);
            user.setActive(active);
            userRepository.update(user, context);
        });
    }
}