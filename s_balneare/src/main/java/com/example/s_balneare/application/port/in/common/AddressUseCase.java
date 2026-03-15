package com.example.s_balneare.application.port.in.common;

import com.example.s_balneare.application.service.common.AddressService;
import com.example.s_balneare.domain.common.Address;

import java.util.List;

/**
 * Interfaccia che racchiude tutti i metodi che un Service deve avere per manipolare un oggetto di tipo Address.
 * <p>Implementata in:
 *
 * @see AddressService AddressService
 */
public interface AddressUseCase {
    //manipolazione
    Integer createAddress(Address address);
    void updateAddress(Integer id, Address address);

    //letture
    Address getAddress(Integer id);
    List<Address> getAddressesByCity(String city);
    List<Address> getAddressesByCountry(String country);
}