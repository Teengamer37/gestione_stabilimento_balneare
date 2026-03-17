package com.example.s_balneare.domain.beach;

/// Contiene l'inventario degli oggetti disponibili in una spiaggia
public record BeachInventory(
        int countExtraSdraio,
        int countExtraLettini,
        int countExtraSedie,
        int countCamerini
) {
    //costruttore compatto per assicurarsi l'integrità dei valori
    public BeachInventory {
        if (countExtraSdraio < 0 || countExtraLettini < 0 || countExtraSedie < 0 || countCamerini < 0) {
            throw new IllegalArgumentException("ERROR: inventory counts cannot be negative");
        }
    }

    //metodo Factory statico per inizializzare l'oggetto beachInventory
    public static BeachInventory empty() {
        return new BeachInventory(0, 0, 0, 0);
    }

    //metodo Builder per costruire l'oggetto col pattern Builder
    public static Builder builder() {
        return new Builder();
    }

    //metodi wither
    public BeachInventory withCountExtraSdraio(int countExtraSdraio) {
        return new BeachInventory(countExtraSdraio, countExtraLettini, countExtraSedie, countCamerini);
    }
    public BeachInventory withCountExtraLettini(int countExtraLettini) {
        return new BeachInventory(countExtraSdraio, countExtraLettini, countExtraSedie, countCamerini);
    }
    public BeachInventory withCountExtraSedie(int countExtraSedie) {
        return new BeachInventory(countExtraSdraio, countExtraLettini, countExtraSedie, countCamerini);
    }
    public BeachInventory withCountCamerini(int countCamerini) {
        return new BeachInventory(countExtraSdraio, countExtraLettini, countExtraSedie, countCamerini);
    }

    //pattern Builder per creazione BeachInventory facilitata
    public static class Builder {
        private int countExtraSdraio = 0;
        private int countExtraLettini = 0;
        private int countExtraSedie = 0;
        private int countCamerini = 0;

        public Builder() {
        }

        //costruttore copia
        public Builder(BeachInventory original) {
            this.countExtraSdraio = original.countExtraSdraio;
            this.countExtraLettini = original.countExtraLettini;
            this.countExtraSedie = original.countExtraSedie;
            this.countCamerini = original.countCamerini;
        }

        public Builder countExtraSdraio(int val) {
            countExtraSdraio = val;
            return this;
        }

        public Builder countExtraLettini(int val) {
            countExtraLettini = val;
            return this;
        }

        public Builder countExtraSedie(int val) {
            countExtraSedie = val;
            return this;
        }

        public Builder countCamerini(int val) {
            countCamerini = val;
            return this;
        }

        public BeachInventory build() {
            return new BeachInventory(countExtraSdraio, countExtraLettini, countExtraSedie, countCamerini);
        }
    }
}