package com.studyNook.member.dto;

public record MemberTokenDto(String accessToken, String refreshToken) {
    public static MemberTokenDto of(String accessToken, String refreshToken) {
        return new MemberTokenDto(accessToken, refreshToken);
    }
}
