package com.example.s_balneare.domain.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest extends UserTest<Customer> {
    @Override
    protected Customer createValidUser() {
        return new Customer(1, "customer@test.com", "cust_user", "Mario", "Rossi", "+393331234567", 1, true);
    }

    @Override
    protected Customer createInvalidUser(String email, String username, String name, String surname) {
        return new Customer(1, email, username, name, surname, "+393331234567", 1, true);
    }

    // ==========================================
    // TEST SPECIFICI PER CUSTOMER
    // ==========================================

    @Test
    void constructor_SetsFieldsCorrectly() {
        Customer customer = new Customer(1, "test@test.com", "user", "Nome", "Nomignolo", "+3912345678", 50, true);

        assertEquals("+3912345678", customer.getPhoneNumber());
        assertEquals(50, customer.getAddressId());
        assertTrue(customer.isActive());
        assertEquals(Role.CUSTOMER, customer.getRole());
        assertFalse(customer.isOTP());
    }

    @Test
    void changePhoneNumber_UpdatesCorrectly_AndValidatesFormat() {
        Customer customer = createValidUser();

        customer.changePhoneNumber("+39 333 999 9999");
        assertEquals("+393339999999", customer.getPhoneNumber());

        assertThrows(IllegalArgumentException.class, () -> customer.changePhoneNumber("iPhone di Marco"));
        assertThrows(IllegalArgumentException.class, () -> customer.changePhoneNumber("   "));
        assertThrows(IllegalArgumentException.class, () -> customer.changePhoneNumber(null));
    }

    @Test
    void closeAccount_SetsActiveToFalse() {
        Customer customer = createValidUser();
        assertTrue(customer.isActive());

        customer.closeAccount();

        assertFalse(customer.isActive());
    }

    @Test
    void constructor_ThrowsException_IfPhoneNumberIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new Customer(1, "a@b.com", "m.m.l.s", "nome strano", "strano nome", "12345", 1, true));
    }
}