package com.example.s_balneare.domain.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OwnerTest extends UserTest<Owner> {

    @Override
    protected Owner createValidUser() {
        return new Owner(1, "owner@beach.it", "owner_user", "Marco", "Soldi", true, true);
    }

    @Override
    protected Owner createInvalidUser(String email, String username, String name, String surname) {
        return new Owner(1, email, username, name, surname, true, true);
    }

    // ==========================================
    // TEST SPECIFICI PER OWNER
    // ==========================================

    @Test
    void constructor_SetsFieldsCorrectly() {
        Owner owner = new Owner(1, "test@test.com", "user", "Uomo", "Soldi", true, false);

        assertEquals(Role.OWNER, owner.getRole());
        assertTrue(owner.isActive());
        assertFalse(owner.isOTP());
    }

    @Test
    void updateOTP_TogglesCorrectly() {
        Owner owner = createValidUser();
        assertTrue(owner.isOTP());

        owner.updateOTP(false);
        assertFalse(owner.isOTP());

        owner.updateOTP(true);
        assertTrue(owner.isOTP());
    }

    @Test
    void closeAccount_SetsActiveToFalse() {
        Owner owner = createValidUser();
        assertTrue(owner.isActive());

        owner.closeAccount();

        assertFalse(owner.isActive());
    }
}