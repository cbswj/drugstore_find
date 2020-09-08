package com.school_drugstore;


public class drugstore_MyItem {

    private String Address;
    private String title;
    private String Tel;
    private String x;
    private String y;
    private int star;

    public drugstore_MyItem(String title, String address, String Tel, String x, String y, int star) {
        this.title = title;
        this.Address = address;
        this.Tel = Tel;
        this.x = x;
        this.y = y;
        this.star = star;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getAddress() {
        return Address;
    }

    public String getTitle() {
        return title;
    }

    public String getTel() {
        return Tel;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTel(String tel) {
        this.Tel = tel;
    }
}