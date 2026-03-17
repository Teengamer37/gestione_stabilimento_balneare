package com.example.s_balneare.domain.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdminTest extends UserTest<Admin> {

    @Override
    protected Admin createValidUser() {
        return new Admin(1, "admin@beach.it", "admin_user", "John", "Doe", true);
    }

    @Override
    protected Admin createInvalidUser(String email, String username, String name, String surname) {
        return new Admin(1, email, username, name, surname, true);
    }

    // ==========================================
    // TEST SPECIFICI PER ADMIN
    // ==========================================

    @Test
    void constructor_SetsFieldsCorrectly() {
        Admin admin = new Admin(1, "test@test.com", "admin", "Hackerino", "Birichino", false);

        assertEquals(Role.ADMIN, admin.getRole());
        assertFalse(admin.isOTP());
    }

    @Test
    void updateOTP_TogglesCorrectly() {
        Admin admin = createValidUser();
        assertTrue(admin.isOTP());

        admin.updateOTP(false);
        assertFalse(admin.isOTP());

        admin.updateOTP(true);
        assertTrue(admin.isOTP());
    }
}