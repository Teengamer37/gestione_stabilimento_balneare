package com.example.s_balneare.domain.beach;

public class Parking {
    private final int id;
    private int nAutoPark;
    private int nMotoPark;
    private int nBikePark;
    private boolean electric;
    private boolean CCTV;

    public Parking(int id, int nAutoPark, int nMotoPark, int nBikePark, boolean electric, boolean CCTV, double priceHour, double priceDay) {
        this.id = id;
        this.nAutoPark = nAutoPark;
        this.nMotoPark = nMotoPark;
        this.nBikePark = nBikePark;
        this.electric = electric;
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

    public boolean isElectric() {
        return electric;
    }

    public void setElectric(boolean electric) {
        this.electric = electric;
    }

    public boolean isCCTV() {
        return CCTV;
    }

    public void setCCTV(boolean CCTV) {
        this.CCTV = CCTV;
    }
}