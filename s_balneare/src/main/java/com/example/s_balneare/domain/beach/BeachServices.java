package com.example.s_balneare.domain.beach;

public class BeachServices {
    private boolean bathrooms;
    private boolean showers;
    private boolean pool;
    private boolean bar;
    private boolean restaurant;
    private boolean wifi;
    private boolean volleyballField;

    public BeachServices(boolean bathrooms, boolean showers, boolean pool, boolean bar, boolean restaurant, boolean wifi, boolean volleyballField) {
        this.bathrooms = bathrooms;
        this.showers = showers;
        this.pool = pool;
        this.bar = bar;
        this.restaurant = restaurant;
        this.wifi = wifi;
        this.volleyballField = volleyballField;
    }

    public boolean hasBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(boolean bathrooms) {
        this.bathrooms = bathrooms;
    }

    public boolean hasShowers() {
        return showers;
    }

    public void setShowers(boolean showers) {
        this.showers = showers;
    }

    public boolean hasPool() {
        return pool;
    }

    public void setPool(boolean pool) {
        this.pool = pool;
    }

    public boolean hasBar() {
        return bar;
    }

    public void setBar(boolean bar) {
        this.bar = bar;
    }

    public boolean hasRestaurant() {
        return restaurant;
    }

    public void setRestaurant(boolean restaurant) {
        this.restaurant = restaurant;
    }

    public boolean hasWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public boolean hasVolleyballField() {
        return volleyballField;
    }

    public void setVolleyballField(boolean volleyballField) {
        this.volleyballField = volleyballField;
    }
}
