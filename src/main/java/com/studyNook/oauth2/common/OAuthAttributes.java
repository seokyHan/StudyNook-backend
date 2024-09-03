package com.studyNook.oauth2.common;

import com.studyNook.global.common.exception.CustomException;
import com.studyNook.member.repository.entity.Member;
import com.studyNook.oauth2.types.SocialType;
import com.studyNook.oauth2.userInfo.GoogleOAuth2UserInfo;
import com.studyNook.oauth2.userInfo.KakaoOAuth2UserInfo;
import com.studyNook.oauth2.userInfo.NaverOAuth2UserInfo;
import com.studyNook.oauth2.userInfo.OAuth2UserInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

import static com.studyNook.global.common.exception.code.AuthResponseCode.UNAUTHORIZED;

@Getter
public class OAuthAttributes {

    private String nameAttributeKey;
    private OAuth2UserInfo oAuth2UserInfo;

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo){
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    public static OAuthAttributes of(SocialType socialType, String attributeName, Map<String, Object> attributes){
        return switch (socialType) {
            case GOOGLE -> ofGoogle(attributeName, attributes);
            case NAVER -> ofNaver(attributeName, attributes);
            case KAKAO -> ofKakao(attributeName, attributes);
        };
    }

    private static OAuthAttributes ofKakao(String attributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(attributeName)
                .oAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofGoogle(String attributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(attributeName)
                .oAuth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofNaver(String attributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(attributeName)
                .oAuth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .build();
    }

    public Member toMemberEntity(SocialType socialType, OAuth2UserInfo oAuth2UserInfo){
        return Member.builder()
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getNickname())
                .nickName(oAuth2UserInfo.getNickname())
                .imageUrl(oAuth2UserInfo.getImageUrl())
                .socialType(socialType)
                .socialId(oAuth2UserInfo.getId())
                .build();
    }
}
