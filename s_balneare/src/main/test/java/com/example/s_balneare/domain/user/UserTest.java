package com.example.s_balneare.domain.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public abstract class UserTest<T extends User> {
    // ==========================================
    // TEST DEL COSTRUTTORE E VALIDAZIONE
    // ==========================================

    @Test
    void constructor_ThrowsException_IfEmailIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> createInvalidUser("   ", "user", "Marco", "Marchetto"));
        assertThrows(IllegalArgumentException.class, () -> createInvalidUser("a@b.c", "user", "Marco", "Marchetto"));
        assertThrows(IllegalArgumentException.class, () -> createInvalidUser("invalid_email", "user", "Marco", "Marchetto"));
    }

    @Test
    void constructor_ThrowsException_IfNameOrSurnameAreInvalid() {
        assertThrows(IllegalArgumentException.class, () -> createInvalidUser("test@email.com", "user", "", "Soldino"));
        assertThrows(IllegalArgumentException.class, () -> createInvalidUser("test@email.com", "user", "Leo", "   "));
    }

    @Test
    void constructor_ThrowsException_IfUsernameIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> createInvalidUser("test@email.com", null, "Mario", "Rossi"));
        assertThrows(IllegalArgumentException.class, () -> createInvalidUser("test@email.com", "a".repeat(51), "Mario", "Rossi"));
    }

    // ==========================================
    // TEST METODI DI BUSINESS
    // ==========================================

    @Test
    void updateEmail_UpdatesCorrectly_AndThrowsOnInvalid() {
        T user = createValidUser();

        user.updateEmail("marco@email.com");
        assertEquals("marco@email.com", user.getEmail());

        // Errore
        assertThrows(IllegalArgumentException.class, () -> user.updateEmail("marco"));
    }

    @Test
    void updateName_UpdatesCorrectly_AndThrowsOnInvalid() {
        T user = createValidUser();

        user.updateName("Leo");
        assertEquals("Leo", user.getName());

        assertThrows(IllegalArgumentException.class, () -> user.updateName(""));
    }

    @Test
    void updateSurname_UpdatesCorrectly_AndThrowsOnInvalid() {
        T user = createValidUser();

        user.updateSurname("Soldino");
        assertEquals("Soldino", user.getSurname());

        assertThrows(IllegalArgumentException.class, () -> user.updateSurname(null));
    }

    @Test
    void updateUsername_UpdatesCorrectly_AndThrowsOnInvalid() {
        T user = createValidUser();

        user.updateUsername("s.l.m.m");
        assertEquals("s.l.m.m", user.getUsername());

        assertThrows(IllegalArgumentException.class, () -> user.updateUsername("   "));
    }

    //metodi helper astratti per creare utenti (richiede l'implementazione specifica)
    protected abstract T createValidUser();
    protected abstract T createInvalidUser(String email, String username, String name, String surname);
}