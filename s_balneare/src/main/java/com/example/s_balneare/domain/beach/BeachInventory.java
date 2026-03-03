package com.example.s_balneare.domain.beach;

public record BeachInventory(
        int countOmbrelloni,
        int countTende,
        int countExtraSdraio,
        int countExtraLettini,
        int countExtraSedie,
        int countCamerini
) {
    //costruttore compatto per assicurarsi l'integrità dei valori
    public BeachInventory {
        if (countOmbrelloni < 0 || countTende < 0 || countExtraSdraio < 0 ||
                countExtraLettini < 0 || countExtraSedie < 0 || countCamerini < 0) {
            throw new IllegalArgumentException("Inventory counts cannot be negative.");
        }
    }

    //metodo Factory statico per inizializzare l'oggetto beachInventory
    public static BeachInventory empty() {
        return new BeachInventory(0, 0, 0, 0, 0, 0);
    }

    //metodi wither
    public BeachInventory withCountOmbrelloni(int countOmbrelloni) {
        return new BeachInventory(countOmbrelloni, countTende, countExtraSdraio, countExtraLettini, countExtraSedie, countCamerini);
    }
    public BeachInventory withCountTende(int countTende) {
        return new BeachInventory(countOmbrelloni, countTende, countExtraSdraio, countExtraLettini, countExtraSedie, countCamerini);
    }
    public BeachInventory withCountExtraSdraio(int countExtraSdraio) {
        return new BeachInventory(countOmbrelloni, countTende, countExtraSdraio, countExtraLettini, countExtraSedie, countCamerini);
    }
    public BeachInventory withCountExtraLettini(int countExtraLettini) {
        return new BeachInventory(countOmbrelloni, countTende, countExtraSdraio, countExtraLettini, countExtraSedie, countCamerini);
    }
    public BeachInventory withCountExtraSedie(int countExtraSedie) {
        return new BeachInventory(countOmbrelloni, countTende, countExtraSdraio, countExtraLettini, countExtraSedie, countCamerini);
    }
    public BeachInventory withCountCamerini(int countCamerini) {
        return new BeachInventory(countOmbrelloni, countTende, countExtraSdraio, countExtraLettini, countExtraSedie, countCamerini);
    }

    //metodo Builder per costruire l'oggetto col pattern Builder
    public static Builder builder() {
        return new Builder();
    }

    //pattern Builder per creazione BeachInventory facilitata
    public static class Builder {
        private int countOmbrelloni = 0;
        private int countTende = 0;
        private int countExtraSdraio = 0;
        private int countExtraLettini = 0;
        private int countExtraSedie = 0;
        private int countCamerini = 0;

        public Builder countOmbrelloni(int val) { countOmbrelloni = val; return this; }
        public Builder countTende(int val) { countTende = val; return this; }
        public Builder countExtraSdraio(int val) { countExtraSdraio = val; return this; }
        public Builder countExtraLettini(int val) { countExtraLettini = val; return this; }
        public Builder countExtraSedie(int val) { countExtraSedie = val; return this; }
        public Builder countCamerini(int val) { countCamerini = val; return this; }

        public BeachInventory build() {
            return new BeachInventory(countOmbrelloni, countTende, countExtraSdraio, countExtraLettini, countExtraSedie, countCamerini);
        }
    }
}