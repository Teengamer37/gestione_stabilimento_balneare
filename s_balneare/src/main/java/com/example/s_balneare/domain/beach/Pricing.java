package com.example.s_balneare.domain.beach;

public class Pricing {
    private final int id;
    private double priceOmbrelloni;
    private double priceTende;
    private double priceLettini;
    private double priceSdraio;
    private double priceSedie;
    private double priceParcheggio;
    private double priceChangingRoom;

    public Pricing(int id, double priceOmbrelloni, double priceTende, double priceLettini, double priceSdraio, double priceSedie, double priceParcheggio,  double priceChangingRoom) {
        this.id = id;
        this.priceOmbrelloni = priceOmbrelloni;
        this.priceTende = priceTende;
        this.priceLettini = priceLettini;
        this.priceSdraio = priceSdraio;
        this.priceSedie = priceSedie;
        this.priceParcheggio = priceParcheggio;
        this.priceChangingRoom = priceChangingRoom;
    }

    public int getId() {
        return id;
    }

    public double getPriceOmbrelloni() {
        return priceOmbrelloni;
    }

    public void setPriceOmbrelloni(double priceOmbrelloni) {
        this.priceOmbrelloni = priceOmbrelloni;
    }

    public double getPriceTende() {
        return priceTende;
    }

    public void setPriceTende(double priceTende) {
        this.priceTende = priceTende;
    }

    public double getPriceLettini() {
        return priceLettini;
    }

    public void setPriceLettini(double priceLettini) {
        this.priceLettini = priceLettini;
    }

    public double getPriceSdraio() {
        return priceSdraio;
    }

    public void setPriceSdraio(double priceSdraio) {
        this.priceSdraio = priceSdraio;
    }

    public double getPriceSedie() {
        return priceSedie;
    }

    public void setPriceSedie(double priceSedie) {
        this.priceSedie = priceSedie;
    }

    public double getPriceParcheggio() {
        return priceParcheggio;
    }

    public void setPriceParcheggio(double priceParcheggio) {
        this.priceParcheggio = priceParcheggio;
    }

    public double getPriceChangingRoom() {return priceChangingRoom;}

    public void setPriceChangingRoom(double priceChangingRoom) {this.priceChangingRoom = priceChangingRoom;}
}