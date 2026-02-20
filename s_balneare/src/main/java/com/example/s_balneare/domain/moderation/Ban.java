package com.example.s_balneare.domain.moderation;

import java.time.Instant;
import java.util.UUID;

public class Ban {
    private final UUID id;
    private final UUID bannedID;
    private final BanType banType;
    private final UUID bannedFromBeachID;
    private final UUID adminID;

    private final String reason;
    private final Instant createdAt;

    public Ban(UUID id, UUID bannedID, BanType banType, UUID bannedFromBeachID, UUID adminID, String reason) {
        if (banType == BanType.BEACH && bannedFromBeachID == null) {
            throw new IllegalArgumentException("ERROR: bannedFromBeachID must be set for BEACH ban");
        }

        if (banType == BanType.APPLICATION && bannedFromBeachID != null) {
            this.bannedFromBeachID = null;
        } else {
            this.bannedFromBeachID = bannedFromBeachID;
        }

        if (reason == null) {
            throw new IllegalArgumentException("ERROR: reason must be set");
        }

        this.id = id;
        this.bannedID = bannedID;
        this.banType = banType;
        this.adminID = adminID;
        this.reason = reason;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getBannedID() {
        return bannedID;
    }

    public BanType getBanType() {
        return banType;
    }

    public UUID getBannedFromBeachID() {
        return bannedFromBeachID;
    }

    public UUID getAdminID() {
        return adminID;
    }

    public String getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
