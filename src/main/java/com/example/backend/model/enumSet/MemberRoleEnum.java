package com.example.backend.model.enumSet;

public enum MemberRoleEnum {
    USER("ROLE_USER"),   // 사용자 권한
    ADMIN("ROLE_ADMIN");   // 관리자 권한

    private final String authority;

    MemberRoleEnum(String authority) {
        this.authority = authority;
    }
    public String getAuthority() {
        return this.authority;
    }
}