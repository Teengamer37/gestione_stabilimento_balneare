package com.example.s_balneare.domain.beach;

public record Pricing(
        Integer id,     // = null se è un nuovo pricing
        double priceLettino,
        double priceSdraio,
        double priceSedia,
        double priceParking,
        double priceCamerino
) {
    public Pricing {
        if (priceLettino < 0 || priceSdraio < 0 || priceSedia < 0 ||
                priceParking < 0 || priceCamerino < 0) {
            throw new IllegalArgumentException("ERROR: prices cannot be negative");
        }
    }

    //Static Factory per un Pricing nuovo
    public static Pricing create(double lettino, double sdraio, double sedia, double parking, double camerino) {
        return new Pricing(null, lettino, sdraio, sedia, parking, camerino);
    }

    //metodi wither
    public Pricing withId(Integer id) {
        return new Pricing(id, priceLettino, priceSdraio, priceSedia, priceParking, priceCamerino);
    }
    public Pricing withPriceLettino(double priceLettino) {
        return new Pricing(id, priceLettino, priceSdraio, priceSedia, priceParking, priceCamerino);
    }
    public Pricing withPriceSdraio(double priceSdraio) {
        return new Pricing(id, priceLettino, priceSdraio, priceSedia, priceParking, priceCamerino);
    }
    public Pricing withPriceSedia(double priceSedia) {
        return new Pricing(id, priceLettino, priceSdraio, priceSedia, priceParking, priceCamerino);
    }
    public Pricing withPriceParking(double priceParking) {
        return new Pricing(id, priceLettino, priceSdraio, priceSedia, priceParking, priceCamerino);
    }
    public Pricing withPriceCamerino(double priceCamerino) {
        return new Pricing(id, priceLettino, priceSdraio, priceSedia, priceParking, priceCamerino);
    }

    //pattern Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private double priceLettino;
        private double priceSdraio;
        private double priceSedia;
        private double priceParking;
        private double priceCamerino;

        public Builder() {}

        //costruttore copia
        public Builder(Pricing original) {
            this.id = original.id();
            this.priceLettino = original.priceLettino();
            this.priceSdraio = original.priceSdraio();
            this.priceSedia = original.priceSedia();
            this.priceParking = original.priceParking();
            this.priceCamerino = original.priceCamerino();
        }

        public Builder id(Integer val) { id = val; return this; }
        public Builder priceLettino(double val) { priceLettino = val; return this; }
        public Builder priceSdraio(double val) { priceSdraio = val; return this; }
        public Builder priceSedia(double val) { priceSedia = val; return this; }
        public Builder priceParking(double val) { priceParking = val; return this; }
        public Builder priceCamerino(double val) { priceCamerino = val; return this; }

        public Pricing build() {
            return new Pricing(id, priceLettino, priceSdraio, priceSedia, priceParking, priceCamerino);
        }
    }
}