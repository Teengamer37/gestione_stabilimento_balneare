package com.example.s_balneare.application.service.common;

import com.example.s_balneare.application.port.in.common.AddressUseCase;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.application.port.out.common.AddressRepository;
import com.example.s_balneare.domain.common.Address;
import com.example.s_balneare.domain.common.TransactionContext;

import java.util.List;

/**
 * Implementazione dell'interfaccia che permette la manipolazione della collezione di Address tra l'app Java e il Database.
 * <p>Usa AddressRepository per manipolare l’oggetto Address nel database.
 * <p>Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata.
 *
 * @see AddressUseCase AddressUseCase
 * @see AddressRepository AddressRepository
 * @see TransactionManager TransactionManager per le transazioni SQL
 */
public class AddressService implements AddressUseCase {
    private final AddressRepository addressRepository;
    private final TransactionManager transactionManager;

    public AddressService(AddressRepository addressRepository, TransactionManager transactionManager) {
        this.addressRepository = addressRepository;
        this.transactionManager = transactionManager;
    }

    /**
     * Aggiunta nuovo indirizzo associato a utente/spiaggia nel DB.
     *
     * @param address Indirizzo da aggiungere
     * @return ID univoco generato dal Database
     */
    @Override
    public Integer createAddress(Address address) {
        return transactionManager.executeInTransaction(context -> {
            return addressRepository.save(address, context);
        });
    }

    /**
     * Aggiornamento indirizzo presente nel DB.
     *
     * @param id      Identificatore indirizzo da cercare nel DB
     * @param address Parametri da aggiornare
     */
    @Override
    public void updateAddress(Integer id, Address address) {
        transactionManager.executeInTransaction(context -> {
            Address a = getAddressOrThrow(id, context);

            Address updated = a
                    .withId(id)
                    .withStreet(address.street())
                    .withStreetNumber(address.streetNumber())
                    .withCity(address.city())
                    .withZipCode(address.zipCode())
                    .withCountry(address.country());

            addressRepository.update(updated, context);
        });
    }

    /**
     * Ricerca indirizzo nel DB.
     *
     * @param id Identificatore indirizzo da cercare nel DB
     * @return oggetto Address con quell'ID
     */
    @Override
    public Address getAddress(Integer id) {
        return transactionManager.executeInTransaction(context -> {
            return getAddressOrThrow(id, context);
        });
    }

    /**
     * Ricerca indirizzi nel DB data la città.
     *
     * @param city Nome della città
     * @return Lista di indirizzi che hanno come città quella passata come parametro
     */
    @Override
    public List<Address> getAddressesByCity(String city) {
        return transactionManager.executeInTransaction(context -> {
            return addressRepository.findByCity(city, context);
        });
    }

    /**
     * Ricerca indirizzi nel DB dato il paese.
     *
     * @param country Nome del paese
     * @return Lista di indirizzi che hanno come paese quello passato come parametro
     */
    @Override
    public List<Address> getAddressesByCountry(String country) {
        return transactionManager.executeInTransaction(context -> {
            return addressRepository.findByCountry(country, context);
        });
    }

    /**
     * Metodo privato che serve nelle operazioni sensibili (in questo caso in update):
     * <p>Cerca in DB -> se non trovo la spiaggia, restituisce NULL -> interrompo tutto.
     *
     * @param id      Identificativo indirizzo da cercare
     * @param context Connessione JDBC
     * @return oggetto Address con quell'ID
     * @throws IllegalArgumentException se l'indirizzo non è stato trovato nel DB
     */
    private Address getAddressOrThrow(Integer id, TransactionContext context) {
        return addressRepository.findById(id, context)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Address not found with id: " + id));
    }
}