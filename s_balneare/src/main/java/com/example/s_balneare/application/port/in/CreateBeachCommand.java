package com.example.s_balneare.application.port.in;

import com.example.s_balneare.domain.beach.*;
import com.example.s_balneare.domain.layout.Zone;

import java.util.List;

/**
 * Record che prende come parametri tutti gli attributi di Beach e Address.
 * Usato in:
 * @see com.example.s_balneare.application.port.in.CreateBeachUseCase CreateBeachUseCase
 */
public record CreateBeachCommand(
        //attributi beach
        Integer ownerId,
        BeachGeneral beachGeneral,
        BeachInventory beachInventory,
        BeachServices beachServices,
        Parking parking,
        List<Season> seasons,
        List<Zone> zones,
        String extraInfo,
        boolean active,

        //attributi address
        String street,
        String streetNumber,
        String city,
        String zipCode,
        String country
) {
    //costruttore compatto per assicurarsi l'integrità dei valori
    public CreateBeachCommand {
        if (beachGeneral == null) throw new IllegalArgumentException("ERROR: beachGeneral cannot be null");
    }
}