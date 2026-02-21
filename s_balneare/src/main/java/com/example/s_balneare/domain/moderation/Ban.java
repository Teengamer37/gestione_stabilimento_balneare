package com.example.s_balneare.domain.moderation;

import com.example.s_balneare.domain.beach.Beach;
import com.example.s_balneare.domain.user.AdminUser;
import com.example.s_balneare.domain.user.AppUser;
import com.example.s_balneare.domain.user.Role;

import java.time.Instant;

public class Ban {
    private final int id;
    private final AppUser banned;
    private final BanType banType;
    private final Beach bannedFromBeach;
    private final AdminUser admin;

    private final String reason;
    private final Instant createdAt;

    public Ban(int id, AppUser banned, BanType banType, Beach bannedFromBeach, AdminUser admin, String reason) {
        if (banned.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("ERROR: admin cannot be banned");
        }

        if (banType == BanType.BEACH && bannedFromBeach == null) {
            throw new IllegalArgumentException("ERROR: bannedFromBeachID must be set for BEACH ban");
        }

        if (banType == BanType.APPLICATION && bannedFromBeach != null) {
            this.bannedFromBeach = null;
        } else {
            this.bannedFromBeach = bannedFromBeach;
        }

        if (reason == null) {
            throw new IllegalArgumentException("ERROR: reason must be set");
        }

        this.id = id;
        this.banned = banned;
        this.banType = banType;
        this.admin = admin;
        this.reason = reason;
        this.createdAt = Instant.now();
    }

    public int getId() {
        return id;
    }

    public AppUser getBanned() {
        return banned;
    }

    public BanType getBanType() {
        return banType;
    }

    public Beach getBannedFromBeach() {
        return bannedFromBeach;
    }

    public AdminUser getAdmin() {
        return admin;
    }

    public String getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}