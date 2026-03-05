package com.example.s_balneare.application.port.in;

import com.example.s_balneare.domain.beach.*;

import java.util.List;

public record CreateBeachCommand(
        //attributi beach
        Integer ownerId,
        BeachGeneral beachGeneral,
        BeachInventory beachInventory,
        BeachServices beachServices,
        Parking parking,
        List<Season> seasons,
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