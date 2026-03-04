package com.example.s_balneare.domain.beach;

//TODO: da implementarla nel pattern DDD-lite

public class ZonePricing {
    private final Integer id;
    private final Integer zoneId;
    private double priceOmbrellone;
    private double priceTenda;

    public ZonePricing(Integer id, Integer zoneId, double priceOmbrellone, double priceTenda) {
        if (priceOmbrellone <= 0 || priceTenda <= 0) throw new IllegalArgumentException("ERROR: price(s) must be > 0");

        this.id = id;
        this.zoneId = zoneId;
        this.priceOmbrellone = priceOmbrellone;
        this.priceTenda = priceTenda;
    }

    public Integer getId() {
        return id;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public double getPriceOmbrellone() {
        return priceOmbrellone;
    }

    public void setPriceOmbrellone(double priceOmbrellone) {
        this.priceOmbrellone = priceOmbrellone;
    }

    public double getPriceTenda() {
        return priceTenda;
    }

    public void setPriceTenda(double priceTenda) {
        this.priceTenda = priceTenda;
    }
}