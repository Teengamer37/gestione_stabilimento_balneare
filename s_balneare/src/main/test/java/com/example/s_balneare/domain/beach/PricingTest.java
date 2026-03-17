package com.example.s_balneare.domain.beach;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PricingTest {
    // ==========================================
    // TEST DI VALIDAZIONE
    // ==========================================

    @Test
    void constructor_ThrowsException_IfAnyPriceIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Pricing(1, -1.0, 0, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Pricing(1, 0, -1.0, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Pricing(1, 0, 0, -1.0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Pricing(1, 0, 0, 0, -1.0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Pricing(1, 0, 0, 0, 0, -1.0));
    }

    @Test
    void constructor_AllowsZeroAndNullId() {
        assertDoesNotThrow(() -> new Pricing(1, 0, 0, 0, 0, 0));

        assertDoesNotThrow(() -> new Pricing(null, 10, 10, 10, 10, 10));
    }

    // ==========================================
    // TEST FACTORY E BUILDER
    // ==========================================

    @Test
    void create_ReturnsNewPricingWithNullId() {
        Pricing pricing = Pricing.create(10, 5, 3, 15, 8);

        assertNull(pricing.id());
        assertEquals(10, pricing.priceLettino());
        assertEquals(5, pricing.priceSdraio());
        assertEquals(3, pricing.priceSedia());
        assertEquals(15, pricing.priceParking());
        assertEquals(8, pricing.priceCamerino());
    }

    @Test
    void builder_CreatesPricingCorrectly() {
        Pricing pricing = Pricing.builder()
                .id(101)
                .priceLettino(12.50)
                .priceParking(20.0)
                .build();

        assertEquals(101, pricing.id());
        assertEquals(12.50, pricing.priceLettino());
        assertEquals(20.0, pricing.priceParking());
        assertEquals(0, pricing.priceSdraio());
    }

    @Test
    void builderCopyConstructor_CreatesIndependentCopy() {
        Pricing original = new Pricing(99, 1, 2, 3, 4, 5);

        Pricing copy = Pricing.builder(original).build();
        assertEquals(original, copy);

        Pricing modified = Pricing.builder(original).id(100).build();
        assertNotEquals(original, modified);
        assertEquals(99, original.id());
    }

    // ==========================================
    // TEST WITHER METHODS
    // ==========================================

    @Test
    void withId_ReturnsNewInstance_WithUpdatedId() {
        Pricing original = Pricing.create(10, 10, 10, 10, 10);

        Pricing withId = original.withId(123);

        assertEquals(123, withId.id());
        assertEquals(10, withId.priceLettino());

        assertNull(original.id());
    }

    @Test
    void withPrice_ReturnsNewInstance_AndKeepOriginalImmutable() {
        Pricing original = new Pricing(1, 20, 20, 20, 20, 20);

        Pricing updated = original.withPriceSdraio(25.5);

        assertEquals(25.5, updated.priceSdraio());

        assertEquals(20, original.priceSdraio());

        assertEquals(20, updated.priceLettino());
    }

    // ==========================================
    // TEST DI UGUAGLIANZA
    // ==========================================

    @Test
    void equals_ReturnsTrueForSameValues_AndFalseForDifferentValues() {
        Pricing p1 = new Pricing(1, 10, 5, 3, 15, 8);
        Pricing p2 = new Pricing(1, 10, 5, 3, 15, 8);
        assertEquals(p1, p2);

        Pricing p3 = new Pricing(null, 10, 5, 3, 15, 8);
        assertNotEquals(p1, p3);

        Pricing p4 = new Pricing(1, 99, 5, 3, 15, 8);
        assertNotEquals(p1, p4);
    }
}