package com.example.s_balneare.domain.beach;

/// Definisce le tariffe per gli spot (ombrelloni/tende) in una specifica zona per una stagione
public record ZoneTariff(
        String zoneName,
        double priceOmbrellone,
        double priceTenda
) {
    //costruttore compatto per assicurarsi l'integrità dei valori
    public ZoneTariff {
        if (zoneName == null || zoneName.isBlank())
            throw new IllegalArgumentException("ERROR: zoneName cannot be blank");
        if (priceOmbrellone < 0 || priceTenda < 0)
            throw new IllegalArgumentException("ERROR: prices cannot be negative");
    }

    //Static Factory per un ZoneTariff nuovo
    public static ZoneTariff create(String zoneName, double ombrellone, double tenda) {
        return new ZoneTariff(zoneName, ombrellone, tenda);
    }

    //metodo per usare il pattern Builder per creare/manipolare ZoneTariff
    public static Builder builder() {
        return new Builder();
    }
    public static Builder builder(ZoneTariff zoneTariff) {
        return new Builder(zoneTariff);
    }

    //metodi wither
    public ZoneTariff withZoneName(String zoneName) {
        return new ZoneTariff(zoneName, priceOmbrellone, priceTenda);
    }
    public ZoneTariff withPriceOmbrellone(double priceOmbrellone) {
        return new ZoneTariff(zoneName, priceOmbrellone, priceTenda);
    }
    public ZoneTariff withPriceTenda(double priceTenda) {
        return new ZoneTariff(zoneName, priceOmbrellone, priceTenda);
    }

    //pattern Builder
    public static class Builder {
        private String zoneName;
        private double priceOmbrellone;
        private double priceTenda;

        public Builder() {
        }

        //costruttore copia
        public Builder(ZoneTariff original) {
            this.zoneName = original.zoneName();
            this.priceOmbrellone = original.priceOmbrellone();
            this.priceTenda = original.priceTenda();
        }

        public Builder zoneName(String val) {
            zoneName = val;
            return this;
        }

        public Builder priceOmbrellone(double val) {
            priceOmbrellone = val;
            return this;
        }

        public Builder priceTenda(double val) {
            priceTenda = val;
            return this;
        }

        public ZoneTariff build() {
            return new ZoneTariff(zoneName, priceOmbrellone, priceTenda);
        }
    }
}