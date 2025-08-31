package com.example.heartrateandtemperaturemonitor.dao;

public class User {
    private Integer uid;
    private String uname;
    private String upassword;
    private Integer per;

    public String getJphone() {
        return jphone;
    }

    public void setJphone(String jphone) {
        this.jphone = jphone;
    }

    private String jphone;
    private String createDateTime;

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", uname='" + uname + '\'' +
                ", upassword='" + upassword + '\'' +
                ", per=" + per +
                ", jphone=" + jphone +
                ", createDateTime='" + createDateTime + '\'' +
                '}';
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpassword() {
        return upassword;
    }

    public void setUpassword(String upassword) {
        this.upassword = upassword;
    }

    public Integer getPer() {
        return per;
    }

    public void setPer(Integer per) {
        this.per = per;
    }



    public String getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(String createDateTime) {
        this.createDateTime = createDateTime;
    }
}
