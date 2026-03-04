package com.example.s_balneare.application.service;

import com.example.s_balneare.application.port.out.AddressRepository;
import com.example.s_balneare.domain.common.Address;

import java.util.List;

//contiene metodi per gestire la collezione di addresses salvati nel database
public class AddressService {
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    //aggiunta nuovo indirizzo associato a utente/spiaggia nel DB
    public int createAddress(Address address) {
        return addressRepository.save(address);
    }

    //aggiornamento indirizzo presente nel DB
    public void updateAddress(Integer id, Address address) {
        Address a = getAddressOrThrow(id);

        Address updated = a
                .withId(id)
                .withStreet(address.street())
                .withStreetNumber(address.streetNumber())
                .withCity(address.city())
                .withZipCode(address.zipCode())
                .withCountry(address.country());

        addressRepository.update(updated);
    }

    //ricerca indirizzo nel DB
    public Address getAddress(Integer id) {
        return getAddressOrThrow(id);
    }

    //ricerca indirizzi nel DB data la città
    public List<Address> getAddressesByCity(String city) {
        return addressRepository.findByCity(city);
    }

    //ricerca indirizzi nel DB dato il paese
    public List<Address> getAddressesByCountry(String country) {
        return addressRepository.findByCountry(country);
    }

    //eliminazione indirizzo dal DB
    public void deleteAddress(Integer id) {
        addressRepository.delete(id);
    }

    //metodo privato che serve nelle operazioni sensibili (in questo caso in update)
    //cerca in DB -> se restituisce NULL, allora interrompo tutto
    private Address getAddressOrThrow(Integer id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Address not found with id: " + id));
    }
}