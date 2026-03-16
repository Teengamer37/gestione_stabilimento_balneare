package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.common.AddressRepository;
import com.example.s_balneare.application.port.out.user.UserRepository;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.user.Customer;

import java.time.LocalDate;

/**
 * Implementazione dell'interfaccia che permette la manipolazione del Customer facendo collaborare l'app Java e il Database.<br>
 * Usa AddressRepository per manipolare l’indirizzo;<br>
 * Usa BookingRepository per cancellare tutte le prenotazioni dell’utente in caso di chiusura account.<br>
 * Estende ManageUserService.
 *
 * @see AddressRepository AddressRepository
 * @see BookingRepository BookingRepository
 * @see ManageUserService ManageUserService
 */
public class ManageCustomerService extends ManageUserService<Customer> {
    private final AddressRepository addressRepository;
    private final BookingRepository bookingRepository;

    public ManageCustomerService(UserRepository<Customer> userRepository, AddressRepository addressRepository,
                                 BookingRepository bookingRepository, TransactionManager transactionManager) {
        super(userRepository, transactionManager);
        this.addressRepository = addressRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Aggiorna il numero di telefono dell’utente.
     *
     * @param id              ID dell’utente
     * @param phoneNumber     Nuovo numero di telefono
     * @param currentPassword Password attuale (per convalida modifiche)
     * @throws IllegalArgumentException se l’utente non esiste nel DB/la vecchia password non corrisponde
     */
    public void updatePhoneNumber(Integer id, String phoneNumber, String currentPassword) {
        transactionManager.executeInTransaction(context -> {
            Customer user = getUserOrThrow(id, context);

            String currentHashedPassword = userRepository.findPassword(id, context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: user and/or password not valid"));
            verifyPassword(currentPassword, currentHashedPassword);

            user.changePhoneNumber(phoneNumber);
        });
    }

    //rendo inutilizzabile il metodo updateDatas() per Customer
    @Override
    public void updateDatas(Integer id, String name, String surname, String username) {
        throw new UnsupportedOperationException("ERROR: cannot use updateDatas() on Customer. Please use updateCustomerDatas() instead");
    }

    /**
     * Aggiorna i dati di un Customer (nome, cognome, username, indirizzo).
     *
     * @param id             ID dell’utente
     * @param name           Nuovo nome
     * @param surname        Nuovo cognome
     * @param username       Nuovo username
     * @param newAddressData Nuovo indirizzo
     * @throws IllegalArgumentException se l’utente non esiste nel DB/la vecchia password non corrisponde/l’indirizzo non esiste nel DB
     */
    public void updateCustomerDatas(Integer id, String name, String surname, String username, Address newAddressData) {
        transactionManager.executeInTransaction(context -> {
            //passo 1: aggiorno dati utente
            Customer customer = getUserOrThrow(id, context);
            customer.updateName(name);
            customer.updateSurname(surname);
            customer.updateUsername(username);

            //passo 2: aggiorno indirizzo associato al Customer
            Address currentAddress = addressRepository.findById(customer.getAddressId(), context)
                    .orElseThrow(() -> new IllegalArgumentException("ERROR: address not found"));

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
     * Chiude l'account di un Customer:<br>
     * Verifica la password, annulla tutte le prenotazioni future, disattiva l'account
     *
     * @param customerId      ID del Customer da chiudere
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