package com.example.s_balneare.application.port.in;

import com.example.s_balneare.domain.beach.BeachGeneral;
import com.example.s_balneare.domain.beach.BeachInventory;
import com.example.s_balneare.domain.beach.BeachServices;
import com.example.s_balneare.domain.beach.Parking;

import java.util.List;

public record CreateBeachCommand(
        //attributi beach
        int ownerId,
        BeachGeneral beachGeneral,
        BeachInventory beachInventory,
        BeachServices beachServices,
        Parking parking,
        List<Integer> seasonIds,
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
        if (ownerId < 0) throw new IllegalArgumentException("ERROR: ownerId not valid");
        if (beachGeneral == null) throw new IllegalArgumentException("ERROR: beachGeneral cannot be null");
        if (extraInfo == null) extraInfo = "";

        if (isNullOrBlank(street) || isNullOrBlank(streetNumber) || isNullOrBlank(city) || isNullOrBlank(zipCode) || isNullOrBlank(country))
            throw new IllegalArgumentException("ERROR: one or more fields for Address are null/blank");
    }

    private static boolean isNullOrBlank(String s) {
        return s == null || s.isBlank();
    }
}
