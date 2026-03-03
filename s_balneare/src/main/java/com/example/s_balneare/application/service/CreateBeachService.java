package com.example.s_balneare.application.service;

import com.example.s_balneare.application.port.in.CreateBeachCommand;
import com.example.s_balneare.application.port.in.CreateBeachUseCase;
import com.example.s_balneare.application.port.out.AddressRepository;
import com.example.s_balneare.application.port.out.BeachRepository;
import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.common.Address;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class CreateBeachService implements CreateBeachUseCase {

    private final AddressRepository addressRepository;
    private final BeachRepository beachRepository;
    private final DataSource dataSource;

    public CreateBeachService(AddressRepository addressRepository, BeachRepository beachRepository, DataSource dataSource) {
        this.addressRepository = addressRepository;
        this.beachRepository = beachRepository;
        this.dataSource = dataSource;
    }

    //crea spiaggia date address e info su beach
    @Override
    public int createBeach(CreateBeachCommand command) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                //passo 1: creazione address e salvataggio nel DB
                Address address = new Address(0, command.street(), command.streetNumber(), command.city(), command.zipCode(), command.country());
                //addressId serve per recuperare l'ID generato dal DB
                int addressId = addressRepository.save(address, connection);

                //passo 2: creazione spiaggia e salvataggio nel DB
                Beach beach = new Beach(0, command.ownerId(), addressId, command.beachGeneral(), command.beachInventory(), command.beachServices(), command.parking(), command.extraInfo(), command.seasonIds(), command.active());
                int beachId = beachRepository.save(beach, connection);

                //passo 3: fine transaction e ritorno il nuovo ID della spiaggia
                connection.commit();
                return beachId;
            } catch (SQLException e) {
                try {
                    //andata male -> ripristino allo stato iniziale
                    connection.rollback();
                } catch (SQLException e2) {
                    e.addSuppressed(e2);
                }
                throw new RuntimeException("ERROR: unable to save beach", e);
            } finally {
                try {
                    //in qualsiasi caso, rimetto autocommit a true
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    System.out.println("WARNING: unable to set autocommit to true");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ERROR: could not establish database connection", e);
        }
    }
}