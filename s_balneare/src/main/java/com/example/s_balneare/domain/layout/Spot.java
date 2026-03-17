package com.example.s_balneare.domain.layout;

/// Definisce un singolo spot all'interno di una zona della spiaggia
public record Spot(
        Integer id,   // = null se Spot nuovo
        SpotType type,
        int row,
        int column
) {
    //costruttore compatto per assicurarsi l'integrità dei valori
    public Spot {
        if (type == null) throw new IllegalArgumentException("ERROR: spot type cannot be null");
        if (row < 0 || column < 0) throw new IllegalArgumentException("ERROR: row and column cannot be negative");
    }

    //Static Factory per uno Spot nuovo
    public static Spot create(SpotType type, int row, int column) {
        return new Spot(null, type, row, column);
    }

    //metodo per usare il pattern Builder per creare/manipolare Spot
    public static Builder builder() {
        return new Builder();
    }
    public static Builder builder(Spot spot) {
        return new Builder(spot);
    }

    //metodi wither
    public Spot withId(Integer newId) {
        return new Spot(newId, type, row, column);
    }
    public Spot withType(SpotType type) {
        return new Spot(id, type, row, column);
    }
    public Spot withRow(int row) {
        return new Spot(id, type, row, column);
    }
    public Spot withColumn(int column) {
        return new Spot(id, type, row, column);
    }

    //pattern Builder
    public static class Builder {
        private Integer id;
        private SpotType type;
        private int row;
        private int column;

        public Builder() {
        }

        //costruttore copia
        public Builder(Spot original) {
            this.id = original.id();
            this.type = original.type();
            this.row = original.row();
            this.column = original.column();
        }

        public Builder id(Integer val) {
            id = val;
            return this;
        }

        public Builder type(SpotType val) {
            type = val;
            return this;
        }

        public Builder row(int val) {
            row = val;
            return this;
        }

        public Builder column(int val) {
            column = val;
            return this;
        }

        public Spot build() {
            return new Spot(id, type, row, column);
        }
    }
}