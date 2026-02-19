package com.example.s_balneare.ui.viewmodel;

import com.example.s_balneare.domain.user.Role;

public final class UiSession {

    private static Role currentRole = null;

    private UiSession() {}

    public static Role getRole() {
        return currentRole;
    }

    public static void setRole(Role role) {
        currentRole = role;
    }

    public static boolean isLoggedIn() {
        return currentRole != null;
    }

    public static void logout() {
        currentRole = null;
    }
}