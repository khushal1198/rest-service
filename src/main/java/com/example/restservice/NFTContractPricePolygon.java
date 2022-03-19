package com.example.restservice;

import java.math.BigInteger;

public class NFTContractPricePolygon {
    private BigInteger number;
    private BigInteger price;

    public BigInteger getNumber() {
        return number;
    }

    public void setNumber(BigInteger number) {
        this.number = number;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }

    public BigInteger getPrice() {
        return price;
    }

    public NFTContractPricePolygon(BigInteger number, BigInteger price) {
        this.number = number;
        this.price = price;
    }


}
