package com.example.backend.model.enumSet;

public enum MemberActiveEnum {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String activity;

    MemberActiveEnum(String activity) {this.activity = activity;}
    public String getActivity() { return this.activity;}
}
