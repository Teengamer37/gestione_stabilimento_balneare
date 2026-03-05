package com.example.s_balneare.domain.beach;

public record ZoneTariff(
        String zoneName,
        double priceOmbrellone,
        double priceTenda
) {
    public ZoneTariff {
        if (zoneName == null || zoneName.isBlank()) throw new IllegalArgumentException("ERROR: zoneName cannot be blank");
        if (priceOmbrellone < 0 || priceTenda < 0) throw new IllegalArgumentException("ERROR: prices cannot be negative");
    }

    //Static Factory per un ZoneTariff nuovo
    public static ZoneTariff create(String zoneName, double ombrellone, double tenda) {
        return new ZoneTariff(zoneName, ombrellone, tenda);
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
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String zoneName;
        private double priceOmbrellone;
        private double priceTenda;

        public Builder() {}

        //costruttore copia
        public Builder(ZoneTariff original) {
            this.zoneName = original.zoneName();
            this.priceOmbrellone = original.priceOmbrellone();
            this.priceTenda = original.priceTenda();
        }

        public Builder zoneName(String val) { zoneName = val; return this; }
        public Builder priceOmbrellone(double val) { priceOmbrellone = val; return this; }
        public Builder priceTenda(double val) { priceTenda = val; return this; }

        public ZoneTariff build() {
            return new ZoneTariff(zoneName, priceOmbrellone, priceTenda);
        }
    }
}