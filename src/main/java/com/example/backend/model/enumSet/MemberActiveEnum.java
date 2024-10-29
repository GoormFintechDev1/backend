package com.example.backend.model.enumSet;

public enum MemberActiveEnum {
    ACTIVE(Activity.ACTIVE),
    INACTIVE(Activity.INACTIVE);

    private final String activity;

    MemberActiveEnum(String activity) {this.activity = activity;}
    public String getActivity() { return this.activity;}

    public static class Activity{
        public static final String ACTIVE = "ACTIVE";
        public static final String INACTIVE = "INACTIVE";
    }
}
