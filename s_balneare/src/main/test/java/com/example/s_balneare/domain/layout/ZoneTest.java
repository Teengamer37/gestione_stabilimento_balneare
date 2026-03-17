package com.example.s_balneare.domain.layout;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ZoneTest {
    // ==========================================
    // TEST DI VALIDAZIONE
    // ==========================================

    @Test
    void constructor_ThrowsException_IfNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Zone(null, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new Zone("   ", List.of()));
    }

    @Test
    void constructor_ThrowsException_IfNameIsTooLong() {
        String tooLongName = "a".repeat(51);
        assertThrows(IllegalArgumentException.class, () -> new Zone(tooLongName, List.of()));
    }

    @Test
    void constructor_EnsuresImmutability_DefensiveCopy() {
        List<Spot> mutableList = new ArrayList<>();
        mutableList.add(Spot.create(SpotType.UMBRELLA, 1, 1));

        Zone zone = new Zone("Zona A", mutableList);

        mutableList.clear();

        assertEquals(1, zone.spots().size());
    }

    // ==========================================
    // TEST FACTORY E BUILDER
    // ==========================================

    @Test
    void create_ReturnsZoneWithEmptyList() {
        Zone zone = Zone.create("Zona B");

        assertEquals("Zona B", zone.name());
        assertTrue(zone.spots().isEmpty());
    }

    @Test
    void builder_CreatesZoneCorrectly() {
        Spot s1 = Spot.create(SpotType.UMBRELLA, 1, 1);

        Zone zone = Zone.builder()
                .name("Zona VIP")
                .spots(List.of(s1))
                .build();

        assertEquals("Zona VIP", zone.name());
        assertEquals(1, zone.spots().size());
        assertEquals(s1, zone.spots().getFirst());
    }

    @Test
    void builderCopyConstructor_CreatesIndependentCopy() {
        Spot s1 = Spot.create(SpotType.UMBRELLA, 1, 1);
        Zone original = Zone.create("Area 1");
        Zone zoneWithSpots = original.withSpots(List.of(s1));

        Zone copy = Zone.builder(zoneWithSpots).build();
        assertEquals(zoneWithSpots, copy);

        Zone modified = Zone.builder(zoneWithSpots).name("Area 2").build();
        assertNotEquals(zoneWithSpots, modified);
        assertEquals("Area 1", zoneWithSpots.name());
    }

    // ==========================================
    // TEST METODI WITHER
    // ==========================================

    @Test
    void withers_ReturnNewInstance_AndKeepOriginalImmutable() {
        Zone original = Zone.create("Zona A");

        Zone renamed = original.withName("Zona B");
        assertEquals("Zona B", renamed.name());
        assertEquals("Zona A", original.name());

        Spot s1 = Spot.create(SpotType.TENT, 2, 2);
        Zone updatedSpots = original.withSpots(List.of(s1));

        assertEquals(1, updatedSpots.spots().size());
        assertTrue(original.spots().isEmpty());
    }

    // ==========================================
    // TEST DI UGUAGLIANZA
    // ==========================================

    @Test
    void equals_ReturnsTrueForSameValues() {
        Zone z1 = Zone.builder().name("Standard").spots(List.of()).build();
        Zone z2 = Zone.builder().name("Standard").spots(List.of()).build();

        assertEquals(z1, z2);

        Zone z3 = z1.withName("VIP");
        assertNotEquals(z1, z3);
    }
}