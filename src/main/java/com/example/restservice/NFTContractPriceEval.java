package com.example.restservice;

public class NFTContractPriceEval {
    private int number;
    private String price;

    public void setNumber(int number) {
        this.number = number;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public String getPrice() {
        return price;
    }

    public NFTContractPriceEval(int number, String price) {
        this.number = number;
        this.price = price;
    }
}
