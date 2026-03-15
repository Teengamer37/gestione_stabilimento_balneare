package com.example.s_balneare.domain.moderation;

import java.time.Instant;

public class Ban {
    private final Integer id;
    private final Integer bannedId;
    private final BanType banType;
    private final Integer bannedFromBeachId;
    private final Integer adminId;

    private final String reason;
    private final Instant createdAt;

    public Ban(Integer id, Integer bannedId, BanType banType, Integer bannedFromBeachId, Integer adminId, String reason, Instant createdAt) {
        checkBannedId(bannedId);
        checkBannedFromBeachId(bannedFromBeachId, banType);
        checkAdminId(adminId);
        checkReason(reason);
        checkCreatedAt(createdAt);
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