package com.example.backend.model.enumSet;

public enum PaymentTypeEnum {
    CARD("CARD"),
    CASH("CASH");

    private final String type;

    PaymentTypeEnum(String Type) {
        this.type = Type;
    }
}
