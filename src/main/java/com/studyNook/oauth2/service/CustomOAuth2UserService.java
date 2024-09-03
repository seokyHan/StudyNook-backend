package com.studyNook.oauth2.service;

import com.studyNook.member.repository.MemberRepository;
import com.studyNook.member.repository.entity.Member;
import com.studyNook.oauth2.common.CustomOAuth2User;
import com.studyNook.oauth2.common.OAuthAttributes;
import com.studyNook.oauth2.types.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static com.studyNook.global.security.jwt.types.Role.ROLE_USER;
import static com.studyNook.oauth2.types.SocialType.fromRegistrationId;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = fromRegistrationId(registrationId);
        String userNameAttributes = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        OAuthAttributes extractAttributes = OAuthAttributes.of(socialType, userNameAttributes, oAuth2User.getAttributes());
        Member member = getOrCreateMember(extractAttributes, socialType);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(ROLE_USER.toString())),
                oAuth2User.getAttributes(),
                extractAttributes.getNameAttributeKey(),
                member.getEmail(),
                ROLE_USER,
                true
        );
    }

    private Member getOrCreateMember(OAuthAttributes attributes, SocialType socialType) {
        String socialId = attributes.getOAuth2UserInfo().getId();
        return memberRepository.findBySocialTypeAndSocialId(socialType, socialId)
                .orElseGet(() -> createNewMember(attributes, socialType));
    }

    private Member createNewMember(OAuthAttributes attributes, SocialType socialType) {
        Member newMember = attributes.toMemberEntity(socialType, attributes.getOAuth2UserInfo());
        return memberRepository.save(newMember);
    }


}
