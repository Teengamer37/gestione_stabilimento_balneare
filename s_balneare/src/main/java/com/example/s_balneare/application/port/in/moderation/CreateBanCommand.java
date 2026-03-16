package com.example.s_balneare.application.port.in.moderation;

import com.example.s_balneare.domain.moderation.BanType;

/**
 * Record che prende come parametri tutti gli attributi di Ban per la creazione di un nuovo ban.<br>
 * Usato in:
 *
 * @see BanUseCase BanUseCase
 */
public record CreateBanCommand(
        Integer bannedId,
        BanType banType,
        Integer bannedFromBeachId,
        Integer adminId,
        String reason
) {
    public CreateBanCommand {
        if (banType == BanType.BEACH && bannedFromBeachId == null) {
            throw new IllegalArgumentException("ERROR: beachId required for BEACH ban");
        }
    }
}