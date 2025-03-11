package com.example.heartrateandtemperaturemonitor.bean;

public class Receive {
    private String hreat;
    private String blood;
    private String temp;
    private String waning;

    @Override
    public String toString() {
        return "Receive{" +
                "hreat='" + hreat + '\'' +
                ", blood='" + blood + '\'' +
                ", temp='" + temp + '\'' +
                ", waning='" + waning + '\'' +
                '}';
    }

    public String getHreat() {
        return hreat;
    }

    public void setHreat(String hreat) {
        this.hreat = hreat;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getWaning() {
        return waning;
    }

    public void setWaning(String waning) {
        this.waning = waning;
    }
}