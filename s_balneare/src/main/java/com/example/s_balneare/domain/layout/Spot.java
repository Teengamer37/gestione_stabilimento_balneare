package com.example.s_balneare.domain.layout;

import com.example.s_balneare.domain.beach.Beach;

public class Spot {
    private final int id;
    private final Beach beach;
    private final SpotType type;

    private final int row;
    private final int column;

    public Spot(int id, Beach beach, SpotType type, int row, int column) {
        this.id = id;
        this.beach = beach;
        this.type = type;
        this.row = row;
        this.column = column;
    }

    public int getId() {
        return id;
    }

    public Beach getBeach() {
        return beach;
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