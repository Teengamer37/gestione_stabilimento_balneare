package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.out.booking.BookingRepository;
import com.example.s_balneare.application.port.out.common.AddressRepository;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ManageCustomerServiceTest extends ManageUserServiceTest<Customer, ManageCustomerService> {
    //mock delle porte di uscita (il database fittizio)
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private BookingRepository bookingRepository;

    //implementazione del metodo createService() che restituisce un'istanza di ManageCustomerService
    @Override
    protected ManageCustomerService createService() {
        //inizializzo il servizio specifico con i suoi repository mockati
        return new ManageCustomerService(userRepository, addressRepository, bookingRepository, transactionManager);
    }

    //implementazione del metodo createValidUser() che restituisce un Customer fittizio
    @Override
    protected Customer createValidUser() {
        //factory per un Customer fittizio
        return new Customer(1, "customer@test.com", "cust_user", "Mario", "Rossi", "+393331112233", 50, true);
    }

    @BeforeEach
    @Override
    void setUp() {
        //inizializzo service, transactionManager e mock di base (findPassword)
        super.setUp();
    }

    // ==========================================
    // OVERRIDE DEL TEST BASE
    // ==========================================

    @Test
    @Override
    void updateDatas_Succeeds_AndSavesUser() {
        //il Customer non deve poter usare updateDatas()
        //sovrascrivo il test della classe astratta per verificare che lanci UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () ->
                service.updateDatas(1, "NewName", "NewSurname", "newUsername")
        );

        //verifico che il database non venga mai toccato
        verify(userRepository, never()).update(any(), any());
    }

    // ==========================================
    // TEST SPECIFICI PER CUSTOMER
    // ==========================================

    @Test
    void updatePhoneNumber_Succeeds_AfterVerifyingPassword() {
        //creo nuovo Customer
        Customer customer = createValidUser();
        when(userRepository.findById(eq(1), any(TransactionContext.class))).thenReturn(Optional.of(customer));

        //chiamo il metodo
        service.updatePhoneNumber(1, "+393339999999", "Password123");

        //mi assicuro che i dati siano stati aggiornati
        assertEquals("+393339999999", customer.getPhoneNumber());
        verify(userRepository).update(eq(customer), any(TransactionContext.class));
    }

    @Test
    void updatePhoneNumber_ThrowsException_IfPasswordIsIncorrect() {
        //creo nuovo Customer
        Customer customer = createValidUser();
        when(userRepository.findById(eq(1), any(TransactionContext.class))).thenReturn(Optional.of(customer));

        //con password sbagliata, deve lanciare eccezione e non aggiornare
        assertThrows(IllegalArgumentException.class, () ->
                service.updatePhoneNumber(1, "+393339999999", "wrong_password")
        );
        verify(userRepository, never()).update(any(), any());
    }

    // ==========================================
    // TEST UPDATE CUSTOMER DATAS
    // ==========================================

    @Test
    void updateCustomerDatas_Succeeds_AndSavesBothAggregates() {
        //creo nuovo Customer e Address
        Customer customer = createValidUser();
        when(userRepository.findById(eq(1), any(TransactionContext.class))).thenReturn(Optional.of(customer));
        Address oldAddress = new Address(50, "Strada delle Marche", "67", "Poggibonsi", "53036", "IT");
        when(addressRepository.findById(eq(50), any(TransactionContext.class))).thenReturn(Optional.of(oldAddress));
        Address newAddressData = new Address(0, "Strada dei Martiri", "80/B", "Firenze", "50121", "IT");

        //aggiorno i dati
        service.updateCustomerDatas(1, "Luigi", "Bianchi", "luigi_b", newAddressData);

        //verifico aggiornamento dati utente
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(userRepository).update(customerCaptor.capture(), any(TransactionContext.class));
        Customer savedCustomer = customerCaptor.getValue();
        assertEquals("Luigi", savedCustomer.getName());
        assertEquals("Bianchi", savedCustomer.getSurname());
        assertEquals("luigi_b", savedCustomer.getUsername());

        //verifico aggiornamento indirizzo
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).update(addressCaptor.capture(), any(TransactionContext.class));
        Address savedAddress = addressCaptor.getValue();
        assertEquals(50, savedAddress.id());
        assertEquals("Strada dei Martiri", savedAddress.street());
        assertEquals("Firenze", savedAddress.city());
    }

    @Test
    void updateCustomerDatas_ThrowsException_IfAddressNotFound() {
        //creo nuovo Customer con indirizzo non esistente
        Customer customer = createValidUser();
        when(userRepository.findById(eq(1), any(TransactionContext.class))).thenReturn(Optional.of(customer));
        //simulo che il DB non trovi l'indirizzo
        when(addressRepository.findById(eq(50), any(TransactionContext.class))).thenReturn(Optional.empty());
        Address newAddressData = new Address(0, "Viale dei Cento", "100", "Centoni", "20100", "IT");

        //deve lanciare eccezione e non aggiornare nulla
        assertThrows(IllegalArgumentException.class, () ->
                service.updateCustomerDatas(1, "Marco", "Benigni", "m.ben", newAddressData)
        );
        verify(addressRepository, never()).update(any(), any());
        verify(userRepository, never()).update(any(), any());
    }

    // ==========================================
    // TEST CLOSE ACCOUNT
    // ==========================================

    @Test
    void closeCustomerAccount_Succeeds_CancelsBookings_AndDeactivatesUser() {
        //creo Customer attivo
        Customer customer = createValidUser();
        assertTrue(customer.isActive());
        when(userRepository.findById(eq(1), any(TransactionContext.class))).thenReturn(Optional.of(customer));

        //chiamo il metodo
        service.closeCustomerAccount(1, "Password123");

        //le prenotazioni future devono essere cancellate
        verify(bookingRepository, times(1)).cancelFutureBookingsForCustomer(eq(1), any(LocalDate.class), any(TransactionContext.class));

        //l'account deve essere aggiornato e disattivato
        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(userRepository).update(captor.capture(), any(TransactionContext.class));
        assertFalse(captor.getValue().isActive());
    }

    @Test
    void closeCustomerAccount_ThrowsException_IfPasswordIsIncorrect() {
        //creo Customer attivo
        Customer customer = createValidUser();
        when(userRepository.findById(eq(1), any(TransactionContext.class))).thenReturn(Optional.of(customer));

        //con password sbagliata, deve lanciare eccezione e non aggiornare
        assertThrows(IllegalArgumentException.class, () ->
                service.closeCustomerAccount(1, "wrong_password")
        );
        verify(bookingRepository, never()).cancelFutureBookingsForCustomer(any(), any(), any());
        verify(userRepository, never()).update(any(), any());
    }
}