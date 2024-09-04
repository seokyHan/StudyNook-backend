package com.studyNook.oauth2.handler;

import com.studyNook.global.common.exception.CustomException;
import com.studyNook.global.security.jwt.TokenProvider;
import com.studyNook.global.security.jwt.types.Role;
import com.studyNook.global.security.jwt.types.TokenType;
import com.studyNook.member.dto.MemberTokenDto;
import com.studyNook.member.repository.MemberRepository;
import com.studyNook.member.repository.entity.Member;
import com.studyNook.oauth2.common.CustomOAuth2User;
import com.studyNook.oauth2.props.OAuth2Properties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import static com.studyNook.global.common.exception.code.AuthResponseCode.USER_NOT_FOUND;
import static com.studyNook.global.security.jwt.types.Role.ROLE_USER;
import static com.studyNook.global.security.jwt.types.TokenType.ACCESS_TOKEN;
import static com.studyNook.global.security.jwt.types.TokenType.REFRESH_TOKEN;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RedisTemplate redisTemplate;
    private final OAuth2Properties oAuth2Properties;

    @Override
    @Transactional(readOnly = true)
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            if(oAuth2User.isFirstLogin()) {
                setCookie(response, "isFirst", "true");
            }

            loginSuccess(response, oAuth2User);
        } catch (Exception e) {
            log.info("social login fail : {}", e.getMessage());
        }
    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        MemberTokenDto memberTokenDto = tokenProvider.generateToken(oAuth2User.getEmail(), ROLE_USER);
        String redisKey = createRedisKey(oAuth2User.getEmail());

        updateRedisToken(redisKey, memberTokenDto.refreshToken());

        Member member = memberRepository.findByEmail(oAuth2User.getEmail()).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "해당 사용자를 찾을 수 없습니다. 관리자 문의 바랍니다."));
        addCookies(response, member, memberTokenDto);

        response.sendRedirect(oAuth2Properties.redirectUrl());
    }

    private String createRedisKey(String email) {
        return String.join("", REFRESH_TOKEN.toString(), email);
    }

    private void updateRedisToken(String redisKey, String refreshToken) {
        String redisRefreshToken = String.valueOf(redisTemplate.opsForValue().get(redisKey));
        if (StringUtils.hasText(redisRefreshToken)) {
            redisTemplate.delete(redisKey);
        }

        redisTemplate.opsForValue().set(redisKey, refreshToken,
                tokenProvider.getExpiration(refreshToken), TimeUnit.MICROSECONDS);
    }

    private void addCookies(HttpServletResponse response, Member member, MemberTokenDto memberTokenDto) throws IOException {
        setCookie(response, "id", member.getId().toString());
        setCookie(response, "name", member.getNickName());
        setCookie(response, "nickName", member.getNickName());
        setCookie(response, "socialLogin", "success");
        setTokenToCookie(response, ACCESS_TOKEN, memberTokenDto.accessToken(),
                oAuth2Properties.accessTokenValidityInMilliseconds(), true, false);
        setTokenToCookie(response, REFRESH_TOKEN, memberTokenDto.refreshToken(),
                oAuth2Properties.refreshTokenValidityInMilliseconds(), true, true);
    }

    private void setCookie(HttpServletResponse response, String name, String value) throws UnsupportedEncodingException {
        ResponseCookie cookie = ResponseCookie.from(name, URLEncoder.encode(value, "UTF-8"))
                .path("/")
                .build();
        response.addHeader(SET_COOKIE, cookie.toString());
    }

    private void setTokenToCookie(HttpServletResponse response, TokenType tokenType, String value,
                                  long maxAge, boolean isSecure, boolean isHttp) throws UnsupportedEncodingException {
        ResponseCookie cookie = ResponseCookie.from(tokenType.getType(), URLEncoder.encode(value, "UTF-8"))
                .maxAge(maxAge)
                .path("/")
                .sameSite("None")
                .secure(isSecure)
                .httpOnly(isHttp)
                .build();
        response.addHeader(SET_COOKIE, cookie.toString());
    }
}
