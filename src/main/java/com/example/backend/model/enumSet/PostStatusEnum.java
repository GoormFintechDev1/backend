package com.example.backend.model.enumSet;

public enum PostStatusEnum {
    ONSALE("ONSALE"),
    RESERVED("RESERVED"),
    SOLDOUT("SOLDOUT");

    private final String postStatus;

    PostStatusEnum(String postStatus) {
        this.postStatus = postStatus;
    }

    public String getPostStatus() {
        return postStatus;
    }

}
