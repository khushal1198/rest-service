package com.example.restservice;

public class NFTContractPricePolygon {
    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public NFTContractPricePolygon(String number, String price) {
        this.number = number;
        this.price = price;
    }

    private String price;
}
