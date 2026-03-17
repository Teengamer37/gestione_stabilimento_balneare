package com.example.s_balneare.domain.beach;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeachInventoryTest {
    // ==========================================
    // TEST DI VALIDAZIONE
    // ==========================================

    @Test
    void constructor_ThrowsException_IfAnyValueIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new BeachInventory(-1, 0, 0, 0), "Sdraio negative");
        assertThrows(IllegalArgumentException.class, () -> new BeachInventory(0, -1, 0, 0), "Lettini negativi");
        assertThrows(IllegalArgumentException.class, () -> new BeachInventory(0, 0, -1, 0), "Sedie negative");
        assertThrows(IllegalArgumentException.class, () -> new BeachInventory(0, 0, 0, -1), "Camerini negativi");
    }

    @Test
    void constructor_AllowsZeroValues() {
        assertDoesNotThrow(() -> new BeachInventory(0, 0, 0, 0));
    }

    // ==========================================
    // TEST FACTORY E BUILDER
    // ==========================================

    @Test
    void empty_CreatesInventoryWithAllZeros() {
        BeachInventory inventory = BeachInventory.empty();
        assertEquals(0, inventory.countExtraSdraio());
        assertEquals(0, inventory.countExtraLettini());
        assertEquals(0, inventory.countExtraSedie());
        assertEquals(0, inventory.countCamerini());
    }

    @Test
    void builder_CreatesInventoryCorrectly() {
        BeachInventory inventory = BeachInventory.builder()
                .countExtraSdraio(10)
                .countExtraLettini(5)
                .build();

        assertEquals(10, inventory.countExtraSdraio());
        assertEquals(5, inventory.countExtraLettini());
        assertEquals(0, inventory.countCamerini());
    }

    @Test
    void builderCopyConstructor_CreatesIndependentCopy() {
        BeachInventory original = new BeachInventory(1, 2, 3, 4);
        BeachInventory.Builder builder = new BeachInventory.Builder(original);
        BeachInventory copy = builder.build();

        assertEquals(original, copy);

        BeachInventory modified = builder.countExtraLettini(99).build();
        assertNotEquals(original, modified);
        assertEquals(2, original.countExtraLettini());
    }

    // ==========================================
    // TEST WITHER METHODS
    // ==========================================

    @Test
    void withers_ReturnNewInstance_AndKeepOriginalImmutable() {
        BeachInventory original = new BeachInventory(10, 10, 10, 10);

        BeachInventory updated = original.withCountExtraSdraio(20);

        assertEquals(20, updated.countExtraSdraio());
        assertEquals(10, original.countExtraSdraio());

        BeachInventory updatedCamerini = original.withCountCamerini(50);
        assertEquals(50, updatedCamerini.countCamerini());
        assertEquals(10, original.countCamerini());
    }
}