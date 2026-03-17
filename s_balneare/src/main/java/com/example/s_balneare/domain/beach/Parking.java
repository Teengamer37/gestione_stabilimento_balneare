package com.example.s_balneare.domain.beach;

/// Descrive la struttura del parcheggio di una spiaggia
public record Parking(
        int nAutoPark,
        int nMotoPark,
        int nElectricPark,
        boolean CCTV
) {
    //costruttore
    public Parking {
        if (nAutoPark < 0 || nMotoPark < 0 || nElectricPark < 0) {
            throw new IllegalArgumentException("ERROR: parking counts cannot be negative");
        }
    }

    //metodo per inizializzare l'oggetto con valori di default
    public static Parking empty() {
        return new Parking(0, 0, 0, false);
    }

    //metodo per usare il pattern Builder per creare/manipolare Parking
    public static Builder builder() {
        return new Builder();
    }

    //metodi wither
    public Parking withNAutoPark(int nAutoPark) {
        return new Parking(nAutoPark, nMotoPark, nElectricPark, CCTV);
    }
    public Parking withNMotoPark(int nMotoPark) {
        return new Parking(nAutoPark, nMotoPark, nElectricPark, CCTV);
    }
    public Parking withNElectricPark(int nElectricPark) {
        return new Parking(nAutoPark, nMotoPark, nElectricPark, CCTV);
    }
    public Parking withCCTV(boolean CCTV) {
        return new Parking(nAutoPark, nMotoPark, nElectricPark, CCTV);
    }

    //pattern Builder
    public static class Builder {
        private int nAutoPark = 0;
        private int nMotoPark = 0;
        private int nElectricPark = 0;
        private boolean CCTV = false;

        public Builder() {
        }

        //costruttore copia
        public Builder(Parking original) {
            this.nAutoPark = original.nAutoPark;
            this.nMotoPark = original.nMotoPark;
            this.nElectricPark = original.nElectricPark;
            this.CCTV = original.CCTV;
        }

        public Builder nAutoPark(int val) {
            nAutoPark = val;
            return this;
        }

        public Builder nMotoPark(int val) {
            nMotoPark = val;
            return this;
        }

        public Builder nElectricPark(int val) {
            nElectricPark = val;
            return this;
        }

        public Builder CCTV(boolean val) {
            CCTV = val;
            return this;
        }

        public Parking build() {
            return new Parking(nAutoPark, nMotoPark, nElectricPark, CCTV);
        }
    }
}