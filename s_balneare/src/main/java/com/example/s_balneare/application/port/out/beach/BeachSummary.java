package com.example.s_balneare.application.port.out.beach;

import com.example.s_balneare.domain.beach.BeachServices;
import com.example.s_balneare.domain.common.Address;

public record BeachSummary(
        Integer beachId,
        Address address,
        String name,
        String city,
        String phoneNumber,
        BeachServices services,
        String extraInfo
) {}