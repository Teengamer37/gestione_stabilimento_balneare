package com.example.s_balneare.domain.beach;

public class Pricing {
    private final int id;
    private double priceLettino;
    private double priceSdraio;
    private double priceSedia;
    private double priceParking;
    private double priceCamerino;

    public Pricing(int id, double priceLettino, double priceSdraio, double priceSedia, double priceParking, double priceCamerino) {
        this.id = id;
        this.priceLettino = priceLettino;
        this.priceSdraio = priceSdraio;
        this.priceSedia = priceSedia;
        this.priceParking = priceParking;
        this.priceCamerino = priceCamerino;
    }

    public int getId() {
        return id;
    }

    public double getPriceLettino() {
        return priceLettino;
    }

    public void setPriceLettino(double priceLettino) {
        this.priceLettino = priceLettino;
    }

    public double getPriceSdraio() {
        return priceSdraio;
    }

    public void setPriceSdraio(double priceSdraio) {
        this.priceSdraio = priceSdraio;
    }

    public double getPriceSedia() {
        return priceSedia;
    }

    public void setPriceSedia(double priceSedia) {
        this.priceSedia = priceSedia;
    }

    public double getPriceParking() {
        return priceParking;
    }

    public void setPriceParking(double priceParking) {
        this.priceParking = priceParking;
    }

    public double getPriceCamerino() {
        return priceCamerino;
    }

    public void setPriceCamerino(double priceCamerino) {
        this.priceCamerino = priceCamerino;
    }
}