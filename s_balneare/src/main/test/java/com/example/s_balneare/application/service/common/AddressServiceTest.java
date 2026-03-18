package com.example.s_balneare.application.service.common;

import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.common.AddressRepository;
import com.example.s_balneare.application.service.TestTransactionManager;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.common.TransactionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    //mock della porta di uscita (il database fittizio)
    @Mock
    private AddressRepository addressRepository;

    //System Under Test (SUT)
    private AddressService addressService;

    @BeforeEach
    void setUp() {
        //uso la utility per bypassare SQL
        TransactionManager transactionManager = new TestTransactionManager();
        addressService = new AddressService(addressRepository, transactionManager);
    }

    // ==========================================
    // TEST CREATE ADDRESS
    // ==========================================

    @Test
    void createAddress_Succeeds_AndReturnsId() {
        //creo nuova Address
        Address newAddress = createValidAddress(0);
        when(addressRepository.save(eq(newAddress), any(TransactionContext.class))).thenReturn(100);

        //chiamo il metodo
        Integer generatedId = addressService.createAddress(newAddress);

        //mi assicuro ritorni ID generato dal DB
        assertEquals(100, generatedId);
        verify(addressRepository).save(eq(newAddress), any(TransactionContext.class));
    }

    // ==========================================
    // TEST UPDATE ADDRESS
    // ==========================================

    @Test
    void updateAddress_Succeeds_WhenAddressExists() {
        //creo Address esistente
        Address existingAddress = createValidAddress(1);
        when(addressRepository.findById(eq(1), any(TransactionContext.class))).thenReturn(Optional.of(existingAddress));

        //preparo l'oggetto con i dati da aggiornare
        Address updateData = new Address(
                0,
                "Via Nuova",
                "99",
                "Milano",
                "20100",
                "Italia"
        );

        //chiamo il metodo
        addressService.updateAddress(1, updateData);

        //catturo l'indirizzo passato al repository per assicurarmi che sia stato ricostruito bene
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).update(addressCaptor.capture(), any(TransactionContext.class));
        Address savedAddress = addressCaptor.getValue();
        assertEquals(1, savedAddress.id());
        assertEquals("Via Nuova", savedAddress.street());
        assertEquals("99", savedAddress.streetNumber());
        assertEquals("Milano", savedAddress.city());
        assertEquals("20100", savedAddress.zipCode());
        assertEquals("Italia", savedAddress.country());
    }

    @Test
    void updateAddress_ThrowsException_WhenAddressDoesNotExist() {
        //creo Address inesistente
        when(addressRepository.findById(eq(99), any(TransactionContext.class))).thenReturn(Optional.empty());

        //cerco di fare update su un indirizzo non esistente
        Address updateData = createValidAddress(0);

        //deve lanciare eccezione e non aggiornare nulla
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                addressService.updateAddress(99, updateData)
        );
        assertTrue(ex.getMessage().contains("not found"));
        verify(addressRepository, never()).update(any(), any());
    }

    // ==========================================
    // TEST GET ADDRESS
    // ==========================================

    @Test
    void getAddress_Succeeds_WhenAddressExists() {
        //creo Address esistente
        Address existingAddress = createValidAddress(5);
        when(addressRepository.findById(eq(5), any(TransactionContext.class))).thenReturn(Optional.of(existingAddress));

        //cerco col metodo
        Address result = addressService.getAddress(5);

        //non deve lanciare eccezione
        assertEquals(existingAddress, result);
        verify(addressRepository).findById(eq(5), any(TransactionContext.class));
    }

    @Test
    void getAddress_ThrowsException_WhenAddressDoesNotExist() {
        //mock del comportamento del repository (non restituisce nulla alla ricerca di un address)
        when(addressRepository.findById(eq(10), any(TransactionContext.class))).thenReturn(Optional.empty());

        //deve lanciare eccezione
        assertThrows(IllegalArgumentException.class, () -> addressService.getAddress(10));
    }

    // ==========================================
    // TEST RICERCHE PER LISTE
    // ==========================================

    @Test
    void getAddressesByCity_ReturnsList() {
        //creo lista di Address
        List<Address> expectedList = List.of(createValidAddress(1), createValidAddress(2));
        when(addressRepository.findByCity(eq("Roma"), any(TransactionContext.class))).thenReturn(expectedList);

        //cerco col metodo
        List<Address> result = addressService.getAddressesByCity("Roma");

        //deve ritornare tutta la lista di Address
        assertEquals(2, result.size());
        verify(addressRepository).findByCity(eq("Roma"), any(TransactionContext.class));

        //cerco stavolta una città diversa
        result = addressService.getAddressesByCity("Viareggio");

        //deve ritornare una lista vuota
        assertEquals(0, result.size());
        verify(addressRepository).findByCity(eq("Viareggio"), any(TransactionContext.class));
    }

    @Test
    void getAddressesByCountry_ReturnsList() {
        //creo lista di Address
        List<Address> expectedList = List.of(createValidAddress(3));
        when(addressRepository.findByCountry(eq("Italia"), any(TransactionContext.class))).thenReturn(expectedList);

        //cerco col metodo
        List<Address> result = addressService.getAddressesByCountry("Italia");

        //deve ritornare tutta la lista di Address
        assertEquals(1, result.size());
        verify(addressRepository).findByCountry(eq("Italia"), any(TransactionContext.class));

        //cerco stavolta una nazione diversa
        result = addressService.getAddressesByCountry("Francia");

        //deve ritornare una lista vuota
        assertEquals(0, result.size());
        verify(addressRepository).findByCountry(eq("Francia"), any(TransactionContext.class));
    }

    // ==========================================
    // HELPER
    // ==========================================

    private Address createValidAddress(int id) {
        return new Address(id, "Via Roma", "1", "Roma", "00100", "Italia");
    }
}