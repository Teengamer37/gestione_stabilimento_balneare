package com.example.s_balneare.application.port.in;

import com.example.s_balneare.domain.common.Address;

import java.util.List;

/**
 * Interfaccia che racchiude tutti i metodi che un Service deve avere per manipolare un oggetto di tipo Address
 * Implementata in:
 * @see com.example.s_balneare.application.service.AddressService AddressService
 */
public interface AddressUseCase {
    Integer createAddress(Address address);
    void updateAddress(Integer id, Address address);
    Address getAddress(Integer id);
    List<Address> getAddressesByCity(String city);
    List<Address> getAddressesByCountry(String country);
}