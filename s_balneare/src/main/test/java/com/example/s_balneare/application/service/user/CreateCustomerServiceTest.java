package com.example.s_balneare.application.service.user;

import com.example.s_balneare.application.port.in.user.CreateCustomerRequest;
import com.example.s_balneare.application.port.out.common.AddressRepository;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.common.TransactionContext;
import com.example.s_balneare.domain.user.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateCustomerServiceTest extends CreateUserServiceTest<Customer, CreateCustomerRequest, CreateCustomerService> {
    //mock della porta di uscita (il database fittizio)
    @Mock
    private AddressRepository addressRepository;

    //implementazione del metodo createService() che restituisce un'istanza di CreateCustomerService
    @Override
    protected CreateCustomerService createService() {
        //inizializzo il servizio specifico con i suoi repository mockati
        return new CreateCustomerService(userRepository, addressRepository, transactionManager);
    }

    //implementazione del metodo createValidRequest() che restituisce una request valida per Customer
    @Override
    protected CreateCustomerRequest createValidRequest() {
        //factory per una request valida specifica per Customer
        return new CreateCustomerRequest(
                "customer@test.com", "cust_user", "Mario", "Rossi", "+393331234567", true,
                "Via Roma", "1", "Roma", "00100", "IT"
        );
    }

    //implementazione del metodo createExpectedUser() che restituisce un Customer fittizio
    @Override
    protected Customer createExpectedUser() {
        //factory per un Customer fittizio
        return new Customer(0, "customer@test.com", "cust_user", "Mario", "Rossi", "+393331234567", 50, false);
    }

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        //setup comune per questo test specifico: quando viene salvato un indirizzo, restituisco un ID fittizio
        when(addressRepository.save(any(Address.class), any(TransactionContext.class))).thenReturn(50);
    }

    // ==========================================
    // TEST SPECIFICI PER CREATE CUSTOMER SERVICE
    // ==========================================

    @Test
    void register_SavesAddressAndCustomer_Correctly() {
        //creo request valida
        CreateCustomerRequest request = createValidRequest();
        String rawPassword = "password";

        //chiamo il metodo
        service.register(request, rawPassword);

        //verifico che il salvataggio dell'indirizzo sia stato chiamato
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository, times(1)).save(addressCaptor.capture(), any(TransactionContext.class));
        assertEquals("Via Roma", addressCaptor.getValue().street());
        assertEquals("Roma", addressCaptor.getValue().city());

        //verifico che l'ID indirizzo restituito sia stato usato per creare il Customer
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(userRepository, times(1)).save(customerCaptor.capture(), anyString(), any(TransactionContext.class));
        assertEquals(50, customerCaptor.getValue().getAddressId());
    }

    @Test
    void register_RollsBack_IfUserSaveFailsAfterAddressSave() {
        //creo request valida
        CreateCustomerRequest request = createValidRequest();
        String rawPassword = "password";

        //simulo un fallimento dopo il salvataggio dell'indirizzo (username duplicato)
        when(userRepository.save(any(Customer.class), anyString(), any(TransactionContext.class)))
                .thenThrow(new IllegalArgumentException("ERROR: username is already in use by another account"));

        //verifico che l'eccezione si propaghi correttamente
        assertThrows(IllegalArgumentException.class, () ->
                service.register(request, rawPassword)
        );

        //verifico anche che il salvataggio dell'indirizzo sia stato tentato
        verify(addressRepository, times(1)).save(any(Address.class), any(TransactionContext.class));
    }
}