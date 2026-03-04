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

    public Ban(Integer id, Integer bannedId, BanType banType, Integer bannedFromBeachId, Integer adminId, String reason) {
        if (bannedId == null ||bannedId <= 0) throw new IllegalArgumentException("ERROR: bannedId not valid");
        if (banType == null) throw new IllegalArgumentException("ERROR: banType not valid");
        if (adminId == null || adminId <= 0) throw new IllegalArgumentException("ERROR: adminId not valid");

        if (banType == BanType.BEACH && (bannedFromBeachId == null || bannedFromBeachId <= 0)) {
            throw new IllegalArgumentException("ERROR: bannedFromBeachID must be set for BEACH ban");
        }

        if (banType == BanType.APPLICATION) {
            this.bannedFromBeachId = null;
        } else {
            this.bannedFromBeachId = bannedFromBeachId;
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("ERROR: reason must be set");
        }

        this.id = id;
        this.bannedId = bannedId;
        this.banType = banType;
        this.adminId = adminId;
        this.reason = reason;
        this.createdAt = Instant.now();
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
}