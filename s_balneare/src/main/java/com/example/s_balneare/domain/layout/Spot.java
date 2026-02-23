package com.example.s_balneare.domain.layout;

public class Spot {
    private final int id;
    private final SpotType type;

    private final int row;
    private final int column;

    public Spot(int id, SpotType type, int row, int column) {
        this.id = id;
        this.type = type;
        this.row = row;
        this.column = column;
    }

    public int getId() {
        return id;
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