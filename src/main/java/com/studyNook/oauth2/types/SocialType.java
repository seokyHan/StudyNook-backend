package com.studyNook.oauth2.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum SocialType {
    GOOGLE("google"),
    NAVER("naver"),
    KAKAO("kakao");

    private final String registrationId;

    public static SocialType fromRegistrationId(String registrationId){
        return Arrays.stream(SocialType.values())
                .filter(type -> type.getRegistrationId().equalsIgnoreCase(registrationId))
                .findFirst()
                .orElse(GOOGLE);
    }
}
