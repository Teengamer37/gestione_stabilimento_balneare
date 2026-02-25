package com.example.s_balneare.domain.moderation;

import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.user.AdminUser;
import com.example.s_balneare.domain.user.AppUser;

import java.time.Instant;

public class Ban {
    private final int id;
    private final int bannedId;
    private final BanType banType;
    private final int bannedFromBeachId;
    private final int adminId;

    private final String reason;
    private final Instant createdAt;

    public Ban(int id, int bannedId, BanType banType, int bannedFromBeachId, int adminId, String reason) {
        if (bannedId <= 0) throw new IllegalArgumentException("ERROR: bannedId not valid");
        if (banType == null) throw new IllegalArgumentException("ERROR: banType not valid");
        if (adminId <= 0) throw new IllegalArgumentException("ERROR: adminId not valid");

        if (banType == BanType.BEACH && bannedFromBeachId <= 0) {
            throw new IllegalArgumentException("ERROR: bannedFromBeachID must be set for BEACH ban");
        }

        if (banType == BanType.APPLICATION) {
            this.bannedFromBeachId = 0;
        } else {
            this.bannedFromBeachId = bannedFromBeachId;
        }

        if (reason == null) {
            throw new IllegalArgumentException("ERROR: reason must be set");
        }

        this.id = id;
        this.bannedId = bannedId;
        this.banType = banType;
        this.adminId = adminId;
        this.reason = reason;
        this.createdAt = Instant.now();
    }

    public int getId() {
        return id;
    }

    public int getBannedId() {
        return bannedId;
    }

    public BanType getBanType() {
        return banType;
    }

    public int getBannedFromBeachId() {
        return bannedFromBeachId;
    }

    public int getAdminId() {
        return adminId;
    }

    public String getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}