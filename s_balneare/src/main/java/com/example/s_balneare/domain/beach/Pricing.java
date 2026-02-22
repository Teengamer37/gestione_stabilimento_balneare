package com.example.s_balneare.domain.beach;

public class Pricing {
    private final int id;
    private double priceOmbrellone;
    private double priceTenda;
    private double priceLettino;
    private double priceSdraio;
    private double priceSedia;
    private double priceParking;
    private double priceChangingRoom;

    public Pricing(int id, double priceOmbrellone, double priceTenda, double priceLettino, double priceSdraio, double priceSedia, double priceParking, double priceChangingRoom) {
        this.id = id;
        this.priceOmbrellone = priceOmbrellone;
        this.priceTenda = priceTenda;
        this.priceLettino = priceLettino;
        this.priceSdraio = priceSdraio;
        this.priceSedia = priceSedia;
        this.priceParking = priceParking;
        this.priceChangingRoom = priceChangingRoom;
    }

    public int getId() {
        return id;
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

    public double getPriceChangingRoom() {
        return priceChangingRoom;
    }

    public void setPriceChangingRoom(double priceChangingRoom) {
        this.priceChangingRoom = priceChangingRoom;
    }
}