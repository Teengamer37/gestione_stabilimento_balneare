package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.AddressRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.user.CustomerRepository;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.user.Customer;

import java.time.LocalDate;

public class ManageCustomerService extends ManageUserService<Customer> {
    private final AddressRepository addressRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;

    public ManageCustomerService(UserRepository<Customer> userRepository, AddressRepository addressRepository,
                                 BookingRepository bookingRepository, TransactionManager transactionManager, CustomerRepository customerRepository) {
        super(userRepository, transactionManager);
        this.addressRepository = addressRepository;
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
    }

    //cambio numero di telefono transazione unica, gestire lato applicazione eventuali messaggi di conferma
    public void updatePhoneNumber(Integer id, String phoneNumber, String currentPassword) {
        transactionManager.executeInTransaction(context -> {
            Customer user = getUserOrThrow(id, context);

            String currentHashedPassword =userRepository.findPassword(id, context)
                            .orElseThrow(() -> new IllegalArgumentException("ERROR: Password not found"));
            verifyPassword(currentPassword, currentHashedPassword);

            user.changePhoneNumber(phoneNumber);
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

    /**
     * Chiude l'account di un Customer:
     * Verifica la password, annulla tutte le prenotazioni future, disattiva l'account
     * @param customerId ID del Customer da chiudere
     * @param currentPassword Password attuale dell'account del Customer
     */
    public void closeCustomerAccount(Integer customerId, String currentPassword) {
        transactionManager.executeInTransaction(context -> {
            //passo 1: prendo il Customer dal DB
            Customer customer = getUserOrThrow(customerId, context);

            //passo 2: verifico la password
            String currentHashedPassword = userRepository.findPassword(customerId, context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: password not found"));
            verifyPassword(currentPassword, currentHashedPassword);

            //passo 3: annullo tutte le prenotazioni future (se e solo se prenotazioni in PENDING e CONFIRMED)
            bookingRepository.cancelFutureBookingsForCustomer(customerId, LocalDate.now(), context);

            //passo 4: disattivo l'account (metto active = false)
            customer.closeAccount();
            userRepository.update(customer, context);
        });
    }
}