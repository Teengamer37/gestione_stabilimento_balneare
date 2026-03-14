package com.example.s_balneare.application.port.out.beach;

import com.example.s_balneare.domain.beach.BeachServices;
import com.example.s_balneare.domain.common.Address;

/**
 * Record che contiene tutti i dati necessari per mostrare all'utente i dettagli di una spiaggia.
 */
public record BeachSummary(
        Integer beachId,
        Address address,
        String name,
        String city,
        String phoneNumber,
        BeachServices services,
        String extraInfo
) {}