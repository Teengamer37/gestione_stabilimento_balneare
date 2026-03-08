package com.example.s_balneare.application.service;

import com.example.s_balneare.application.port.out.AddressRepository;
import com.example.s_balneare.domain.common.Address;

import java.util.List;

/**
 * Implementazione dell'interfaccia che permette la manipolazione della collezione di Address tra l'app Java e il Database.
 */
public class AddressService {
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    /**
     * Aggiunta nuovo indirizzo associato a utente/spiaggia nel DB
     * @param address Indirizzo da aggiungere
     * @return ID univoco generato dal Database
     */
    public int createAddress(Address address) {
        return addressRepository.save(address);
    }

    /**
     * Aggiornamento indirizzo presente nel DB
     * @param id Identificatore indirizzo da cercare nel DB
     * @param address Parametri da aggiornare
     */
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

    /**
     * Ricerca indirizzo nel DB
     * @param id Identificatore indirizzo da cercare nel DB
     * @return oggetto Address con quell'ID
     */
    public Address getAddress(Integer id) {
        return getAddressOrThrow(id);
    }

    /**
     * Ricerca indirizzi nel DB data la città
     * @param city Nome della città
     * @return Lista di indirizzi che hanno come città quella passata come parametro
     */
    public List<Address> getAddressesByCity(String city) {
        return addressRepository.findByCity(city);
    }

    /**
     * Ricerca indirizzi nel DB dato il paese
     * @param country Nome del paese
     * @return Lista di indirizzi che hanno come paese quello passato come parametro
     */
    public List<Address> getAddressesByCountry(String country) {
        return addressRepository.findByCountry(country);
    }

    /**
     * Eliminazione indirizzo dal DB
     * @param id Identificatore indirizzo da eliminare
     */
    public void deleteAddress(Integer id) {
        addressRepository.delete(id);
    }

    /**
     * Metodo privato che serve nelle operazioni sensibili (in questo caso in update):
     * cerca in DB -> se non trovo la spiaggia, restituisce NULL -> interrompo tutto
     * @param id Identificativo indirizzo da cercare
     * @return oggetto Address con quell'ID
     * @throws IllegalArgumentException se l'indirizzo non è stato trovato nel DB
     */
    private Address getAddressOrThrow(Integer id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Address not found with id: " + id));
    }
}