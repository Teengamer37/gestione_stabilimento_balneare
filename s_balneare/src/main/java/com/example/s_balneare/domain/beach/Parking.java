package com.example.s_balneare.domain.beach;

public class Parking {
    private final int id;
    private int nAutoPark;
    private int nMotoPark;
    private int nBikePark;
    private int nElectric;
    private boolean CCTV;

    public Parking(int id, int nAutoPark, int nMotoPark, int nBikePark, int nElectric, boolean CCTV, double priceHour, double priceDay) {
        this.id = id;
        this.nAutoPark = nAutoPark;
        this.nMotoPark = nMotoPark;
        this.nBikePark = nBikePark;
        this.nElectric = nElectric;
        this.CCTV = CCTV;
    }

    public int getId() {
        return id;
    }

    public int getnAutoPark() {
        return nAutoPark;
    }

    public void setnAutoPark(int nAutoPark) {
        this.nAutoPark = nAutoPark;
    }

    public int getnMotoPark() {
        return nMotoPark;
    }

    public void setnMotoPark(int nMotoPark) {
        this.nMotoPark = nMotoPark;
    }

    public int getnBikePark() {
        return nBikePark;
    }

    public void setnBikePark(int nBikePark) {
        this.nBikePark = nBikePark;
    }

    public int getnElectric() {
        return nElectric;
    }

    public void setnElectric(int nElectric) {
        this.nElectric = nElectric;
    }

    public boolean isCCTV() {
        return CCTV;
    }

    public void setCCTV(boolean CCTV) {
        this.CCTV = CCTV;
    }
}