package com.example.s_balneare.application.port.in;

import com.example.s_balneare.domain.moderation.BanType;

import java.time.Instant;

public record CreateBanCommand(
        Integer bannedId,
        BanType banType,
        Integer bannedFromBeachId,
        Integer adminId,
        String reason
) {
}
