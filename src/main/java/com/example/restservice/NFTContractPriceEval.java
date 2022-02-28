package com.example.restservice;

import java.math.BigInteger;

public class NFTContractPriceEval {
    private int number;
    private BigInteger price;

    public void setNumber(int number) {
        this.number = number;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public BigInteger getPrice() {
        return price;
    }

    public NFTContractPriceEval(int number, BigInteger price) {
        this.number = number;
        this.price = price;
    }
}
