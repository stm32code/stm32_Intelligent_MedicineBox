package com.example.heartrateandtemperaturemonitor.bean;

public class Send {
    public void setCmd(Integer cmd) {
        this.cmd = cmd;
    }

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }

    public void setTime3(String time3) {
        this.time3 = time3;
    }
    public String toString() {
        return "Send{" +
                "cmd=" + cmd +
                ", time='" + time + '\'' +
                ", time1='" + time1 + '\'' +
                ", time2='" + time2 + '\'' +
                ", time2='" + time3 + '\'' +
                '}';
    }

    private Integer cmd;
    private String time1, time2,time3;

    public void setTime(String time) {
        this.time = time;
    }

    private String time;
}
