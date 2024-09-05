package com.studyNook.oauth2.service;

import com.studyNook.global.common.exception.CustomException;
import com.studyNook.member.repository.AuthorityRepository;
import com.studyNook.member.repository.MemberAuthorityRepository;
import com.studyNook.member.repository.MemberRepository;
import com.studyNook.member.repository.entity.Authority;
import com.studyNook.member.repository.entity.Member;
import com.studyNook.member.repository.entity.MemberAuthority;
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
import java.util.Optional;

import static com.studyNook.global.common.exception.code.AuthResponseCode.UNAUTHORIZED;
import static com.studyNook.global.security.jwt.types.Role.ROLE_USER;
import static com.studyNook.oauth2.types.SocialType.fromRegistrationId;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final AuthorityRepository authorityRepository;
    private final MemberAuthorityRepository memberAuthorityRepository;

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

        return getOrCreateMember(oAuth2User, extractAttributes, socialType);
    }
    @Transactional(readOnly = true)
    public CustomOAuth2User getOrCreateMember(OAuth2User oAuth2User, OAuthAttributes attributes, SocialType socialType) {
        String socialId = attributes.getOAuth2UserInfo().getId();
        Optional<Member> optionalMember = memberRepository.findBySocialTypeAndSocialId(socialType, socialId);
        Member member = optionalMember.orElseGet(() -> createNewMember(attributes, socialType));

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(ROLE_USER.toString())),
                oAuth2User.getAttributes(),
                attributes.getNameAttributeKey(),
                member.getEmail(),
                !optionalMember.isPresent()
        );
    }
    @Transactional
    public Member createNewMember(OAuthAttributes attributes, SocialType socialType) {
        Member newMember = attributes.toMemberEntity(socialType, attributes.getOAuth2UserInfo());
        Authority authority = authorityRepository.findById(2L).orElseThrow(() -> new CustomException(UNAUTHORIZED, "권한이 존재하지 않습니다."));
        MemberAuthority memberAuthority = MemberAuthority.builder()
                .member(newMember)
                .authority(authority)
                .build();
        memberRepository.save(newMember);
        memberAuthorityRepository.save(memberAuthority);

        return newMember;
    }


}
