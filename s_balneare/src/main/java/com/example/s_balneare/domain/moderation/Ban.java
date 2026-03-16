package com.example.s_balneare.domain.moderation;

import java.time.Instant;

/// Rappresenta un'azione di ban emessa da un amministratore
public record Ban(Integer id, Integer bannedId, BanType banType, Integer bannedFromBeachId, Integer adminId,
                  String reason, Instant createdAt) {
    //costruttore compatto per assicurarsi l'integrità dei valori
    public Ban {
        checkBannedId(bannedId);
        checkBannedFromBeachId(bannedFromBeachId, banType);
        checkAdminId(adminId);
        checkReason(reason);
        checkCreatedAt(createdAt);
    }

    //---- METODI CHECKERS ----
    private void checkBannedId(Integer bannedId) {
        if (bannedId == null || bannedId <= 0) throw new IllegalArgumentException("ERROR: bannedId not valid");
    }

    private void checkBannedFromBeachId(Integer bannedFromBeachId, BanType banType) {
        if (banType == BanType.BEACH && (bannedFromBeachId == null || bannedFromBeachId <= 0))
            throw new IllegalArgumentException("ERROR: BEACH ban must have a valid beachId");
        if (banType == BanType.APPLICATION && bannedFromBeachId != null)
            throw new IllegalArgumentException("ERROR: APPLICATION ban must not have a beachId");
    }

    private void checkAdminId(Integer adminId) {
        if (adminId == null || adminId <= 0) throw new IllegalArgumentException("ERROR: adminId not valid");
    }

    private void checkReason(String reason) {
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("ERROR: reason must be set");
    }

    private void checkCreatedAt(Instant createdAt) {
        if (createdAt == null) throw new IllegalArgumentException("ERROR: createdAt must not be null");
    }
}