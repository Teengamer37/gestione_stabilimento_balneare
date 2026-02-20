package com.example.s_balneare.domain.layout;

import java.util.UUID;

public class Spot {
    private final UUID id;
    private final UUID beachID;
    private final SpotType type;

    private final int row;
    private final int column;

    public Spot(UUID id, UUID beachID, SpotType type, int row, int column) {
        this.id = id;
        this.beachID = beachID;
        this.type = type;
        this.row = row;
        this.column = column;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBeachID() {
        return beachID;
    }

    public SpotType getType() {
        return type;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
