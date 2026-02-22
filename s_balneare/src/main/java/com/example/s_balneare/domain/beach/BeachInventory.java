package com.example.s_balneare.domain.beach;

public class BeachInventory {
    private int countOmbrelloni;
    private int countTende;
    private int countExtraSdraio;
    private int countExtraLettini;
    private int countExtraSedie;
    private int countCamerini;

    public BeachInventory(int countOmbrelloni, int countTende, int countExtraSdraio, int countExtraLettini, int countExtraSedie, int countCamerini) {
        this.countOmbrelloni = countOmbrelloni;
        this.countTende = countTende;
        this.countExtraSdraio = countExtraSdraio;
        this.countExtraLettini = countExtraLettini;
        this.countExtraSedie = countExtraSedie;
        this.countCamerini = countCamerini;
    }

    public int getCountOmbrelloni() {
        return countOmbrelloni;
    }

    public void setCountOmbrelloni(int countOmbrelloni) {
        this.countOmbrelloni = countOmbrelloni;
    }

    public int getCountTende() {
        return countTende;
    }

    public void setCountTende(int countTende) {
        this.countTende = countTende;
    }

    public int getCountExtraSdraio() {
        return countExtraSdraio;
    }

    public void setCountExtraSdraio(int countExtraSdraio) {
        this.countExtraSdraio = countExtraSdraio;
    }

    public int getCountExtraLettini() {
        return countExtraLettini;
    }

    public void setCountExtraLettini(int countExtraLettini) {
        this.countExtraLettini = countExtraLettini;
    }

    public int getCountExtraSedie() {
        return countExtraSedie;
    }

    public void setCountExtraSedie(int countExtraSedie) {
        this.countExtraSedie = countExtraSedie;
    }

    public int getCountCamerini() {return countCamerini;}

    public void setCountCamerini(int countCamerini) {this.countCamerini = countCamerini;}
}