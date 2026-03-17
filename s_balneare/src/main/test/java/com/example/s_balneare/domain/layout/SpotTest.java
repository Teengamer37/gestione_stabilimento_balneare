package com.example.s_balneare.domain.layout;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpotTest {
    // ==========================================
    // TEST DI VALIDAZIONE
    // ==========================================

    @Test
    void constructor_ThrowsException_IfTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Spot(1, null, 1, 1));
    }

    @Test
    void constructor_ThrowsException_IfCoordinatesAreNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Spot(1, SpotType.UMBRELLA, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Spot(1, SpotType.UMBRELLA, 0, -1));
    }

    @Test
    void constructor_AllowsZeroAndPositiveValues() {
        assertDoesNotThrow(() -> new Spot(1, SpotType.UMBRELLA, 0, 0));
        assertDoesNotThrow(() -> new Spot(null, SpotType.TENT, 10, 20));
    }

    // ==========================================
    // TEST FACTORY E BUILDER
    // ==========================================

    @Test
    void create_ReturnsSpotWithNullId() {
        Spot spot = Spot.create(SpotType.UMBRELLA, 5, 5);

        assertNull(spot.id());
        assertEquals(SpotType.UMBRELLA, spot.type());
    }

    @Test
    void builder_CreatesSpotCorrectly() {
        Spot spot = Spot.builder()
                .id(99)
                .type(SpotType.TENT)
                .row(2)
                .column(3)
                .build();

        assertEquals(99, spot.id());
        assertEquals(SpotType.TENT, spot.type());
        assertEquals(2, spot.row());
        assertEquals(3, spot.column());
    }

    @Test
    void builderCopyConstructor_CreatesIndependentInstance() {
        Spot original = new Spot(1, SpotType.UMBRELLA, 1, 1);

        Spot copy = Spot.builder(original).build();
        assertEquals(original, copy);

        Spot modified = Spot.builder(original).type(SpotType.TENT).build();
        assertNotEquals(original, modified);
        assertEquals(SpotType.UMBRELLA, original.type());
    }

    // ==========================================
    // TEST WITHER METHODS
    // ==========================================

    @Test
    void withers_ReturnNewInstance_AndKeepOriginalImmutable() {
        Spot original = new Spot(1, SpotType.UMBRELLA, 0, 0);

        Spot withId = original.withId(500);
        assertEquals(500, withId.id());
        assertEquals(1, original.id());

        Spot withType = original.withType(SpotType.TENT);
        assertEquals(SpotType.TENT, withType.type());
        assertEquals(SpotType.UMBRELLA, original.type());

        Spot withRow = original.withRow(10);
        assertEquals(10, withRow.row());
        assertEquals(0, original.row());
    }

    // ==========================================
    // TEST DI UGUAGLIANZA
    // ==========================================

    @Test
    void equals_ReturnsTrueForIdenticalInstances() {
        Spot s1 = new Spot(1, SpotType.UMBRELLA, 1, 1);
        Spot s2 = new Spot(1, SpotType.UMBRELLA, 1, 1);

        assertEquals(s1, s2);

        Spot s3 = s1.withType(SpotType.TENT);
        assertNotEquals(s1, s3);
    }
}