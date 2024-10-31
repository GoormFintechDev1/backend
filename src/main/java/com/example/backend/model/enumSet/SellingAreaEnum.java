package com.example.backend.model.enumSet;

public enum SellingAreaEnum {
    SELLER("SELLER"),
    BUYER("BUYER"),
    NEGO("NEGO");

    private final String sellingArea;

    SellingAreaEnum(String sellingArea) {
        this.sellingArea = sellingArea;
    }

    public String getSellingArea() {
        return sellingArea;
    }

}
