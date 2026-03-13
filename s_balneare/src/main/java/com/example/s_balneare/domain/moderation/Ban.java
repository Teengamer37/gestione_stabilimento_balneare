package com.example.s_balneare.domain.moderation;

import java.time.Instant;

//TODO: da implementarla nel pattern DDD-lite

public class Ban {
    private final Integer id;
    private final Integer bannedId;
    private final BanType banType;
    private final Integer bannedFromBeachId;
    private final Integer adminId;

    private final String reason;
    private final Instant createdAt;

    //TODO: crea 2 costruttori: uno con createdAt e uno senza

    // 1. Costruttore per NUOVI BAN (senza ID e senza createdAt, usati nell'applicazione)
    public Ban(Integer bannedId, BanType banType, Integer bannedFromBeachId, Integer adminId, String reason) {
        this(null, bannedId, banType, bannedFromBeachId, adminId, reason, Instant.now());
    }

    // 2. Costruttore COMPLETO (usato dal Repository per ricostruire l'oggetto dal DB)
    public Ban(Integer id, Integer bannedId, BanType banType, Integer bannedFromBeachId, Integer adminId, String reason, Instant createdAt) {
        // Eseguiamo i check sui parametri passati, NON sulle variabili d'istanza
        checkBannedId(bannedId);
        checkAdminId(adminId);
        checkReason(reason);
        checkCreatedAt(createdAt);
        // Passiamo entrambi i valori per il check logico incrociato
        checkBannedFromBeachId(bannedFromBeachId, banType);

        this.id = id;
        this.bannedId = bannedId;
        this.banType = banType;
        this.bannedFromBeachId = bannedFromBeachId;
        this.adminId = adminId;
        this.reason = reason;
        this.createdAt = createdAt;
    }


    public Integer getId() {
        return id;
    }

    public Integer getBannedId() {
        return bannedId;
    }

    public BanType getBanType() {
        return banType;
    }

    public Integer getBannedFromBeachId() {
        return bannedFromBeachId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public String getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private void checkBannedId(Integer bannedId) {
        if (bannedId == null || bannedId <= 0) throw new IllegalArgumentException("ERROR: bannedId not valid");
    }

    private void checkBannedFromBeachId(Integer bannedFromBeachId, BanType banType) {
        if (banType == BanType.BEACH && (bannedFromBeachId == null || bannedFromBeachId <= 0)) throw new IllegalArgumentException("ERROR: bannedFromBeachID not valid");
        if (banType == BanType.APPLICATION && bannedFromBeachId != null) throw new IllegalArgumentException("ERROR: bannedFromBeachID not valid");
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