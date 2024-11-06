package com.example.backend.model.enumSet;

public enum TransactionTypeEnum {
    REVENUE("REVENUE"),
    EXPENSE("EXPENSE");

    private final String value;
    TransactionTypeEnum(String value) {
        this.value = value;
    }

}
