package com.example.s_balneare.domain.beach;

import com.example.s_balneare.domain.layout.Zone;

public class ZonePricing {
    private final int id;
    private final int zoneId;
    private double priceOmbrellone;
    private double priceTenda;

    public ZonePricing(int id, int zoneId, double priceOmbrellone, double priceTenda) {
        if (priceOmbrellone <= 0 || priceTenda <= 0) throw new IllegalArgumentException("ERROR: price(s) must be > 0");

        this.id = id;
        this.zoneId = zoneId;
        this.priceOmbrellone = priceOmbrellone;
        this.priceTenda = priceTenda;
    }

    public int getId() {
        return id;
    }

    public int getZoneId() {
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