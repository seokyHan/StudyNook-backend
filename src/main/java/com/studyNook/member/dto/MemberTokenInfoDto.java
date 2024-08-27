package com.studyNook.member.dto;

public record MemberTokenInfoDto(String accessToken, String refreshToken) {

    public static MemberTokenInfoDto of(String accessToken, String refreshToken){
        return new MemberTokenInfoDto(accessToken, refreshToken);
    }
}
