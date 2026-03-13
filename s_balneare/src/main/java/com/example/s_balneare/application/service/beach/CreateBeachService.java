package com.example.s_balneare.application.service.beach;

import com.example.s_balneare.application.port.in.beach.CreateBeachCommand;
import com.example.s_balneare.application.port.in.beach.CreateBeachUseCase;
import com.example.s_balneare.application.port.out.AddressRepository;
import com.example.s_balneare.application.port.out.beach.BeachRepository;
import com.example.s_balneare.application.port.out.TransactionManager;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.common.Address;

/**
 * Implementazione dello Use Case di aggiunta spiaggia nel DB:
 * Interagisce con AddressRepository per salvare l'indirizzo della nuova spiaggia;
 * Successivamente con BeachRepository per salvare la nuova spiaggia con riferimento alla nuova Address.
 * Viene usata la classe TransactionManager per gestire le SQL Transaction in maniera astratta, indipendente dalla libreria utilizzata
 *
 * @see CreateBeachUseCase CreateBeachUseCase
 * @see com.example.s_balneare.application.port.out.TransactionManager TransactionManager
 * @see com.example.s_balneare.application.port.out.AddressRepository AddressRepository
 * @see BeachRepository BeachRepository
 */
public class CreateBeachService implements CreateBeachUseCase {

    private final AddressRepository addressRepository;
    private final BeachRepository beachRepository;
    private final TransactionManager transactionManager;

    public CreateBeachService(AddressRepository addressRepository, BeachRepository beachRepository, TransactionManager transactionManager) {
        this.addressRepository = addressRepository;
        this.beachRepository = beachRepository;
        this.transactionManager = transactionManager;
    }

    //crea spiaggia date address e info su beach
    @Override
    public Integer createBeach(CreateBeachCommand command) {

        //transactionManager pensa in automatico all'apertura transazione, commit e rollback
        return transactionManager.executeInTransaction(context -> {
            Address address = new Address(0, command.street(), command.streetNumber(), command.city(), command.zipCode(), command.country());
            //addressId serve per recuperare l'ID generato dal DB
            Integer addressId = addressRepository.save(address, context);

            //passo 2: creazione spiaggia e salvataggio nel DB
            Beach beach = new Beach(0, command.ownerId(), addressId, command.beachGeneral(), command.beachInventory(), command.beachServices(), command.parking(), command.extraInfo(), command.seasons(), command.zones(), command.active());
            Integer beachId = beachRepository.save(beach, context);

            //passo 3: fine transaction e ritorno il nuovo ID della spiaggia
            return beachId;
        });
    }
}