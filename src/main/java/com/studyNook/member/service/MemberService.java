package com.studyNook.member.service;

import com.studyNook.global.common.exception.CustomException;
import com.studyNook.global.security.jwt.TokenProvider;
import com.studyNook.member.dto.MemberInfoDto;
import com.studyNook.member.dto.MemberTokenDto;
import com.studyNook.member.repository.MemberRepository;
import com.studyNook.member.repository.entity.Member;
import com.studyNook.oauth2.props.OAuth2Properties;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import static com.studyNook.global.common.exception.code.AuthResponseCode.*;
import static com.studyNook.global.security.jwt.types.Role.ROLE_USER;
import static com.studyNook.global.security.jwt.types.TokenType.REFRESH_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final RedisTemplate redisTemplate;
    private final OAuth2Properties oAuth2Properties;

    public ResponseEntity userLogout(HttpServletRequest request) {
        String accessToken = tokenProvider.resolveToken(request);
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        String redisKey = String.join("", REFRESH_TOKEN.toString(), authentication.getName());
        String refreshToken = String.valueOf(redisTemplate.opsForValue().get(redisKey));

        if(StringUtils.isNotEmpty(refreshToken)){
            redisTemplate.delete(redisKey);
        }

        ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        long now = zdt.toInstant().toEpochMilli();
        long expiration = tokenProvider.getExpiration(accessToken);
        updateRedisToken(accessToken, "logout", (expiration - now));

        return ResponseEntity.ok()
                .header(SET_COOKIE, createLogOutCookie().toString())
                .body(null);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<MemberInfoDto> reissue(HttpServletResponse response, String cookieRefreshToken) {
        if(tokenProvider.isTokenExpired(cookieRefreshToken)) {
            throw new CustomException(TOKEN_EXPIRED, "refreshToken 만료");
        }

        Authentication authentication = tokenProvider.getAuthentication(cookieRefreshToken);
        String redisKey = String.join("", REFRESH_TOKEN.toString(), authentication.getName());
        String refreshToken = String.valueOf(redisTemplate.opsForValue().get(redisKey));

        if(StringUtils.isEmpty(refreshToken)){
            redisTemplate.delete(redisKey);
            response.setHeader(SET_COOKIE, createLogOutCookie().toString());
            throw new CustomException(AUTH_FAIL, "redis token 미존재");
        }

        Member member = memberRepository.findByEmail(authentication.getName()).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "해당 사용자를 찾을 수 없습니다. 관리자 문의 바랍니다."));;
        MemberTokenDto tokenDto = tokenProvider.generateToken(authentication.getName(), ROLE_USER);
        MemberInfoDto memberInfoDto = MemberInfoDto.of(tokenDto.accessToken(), tokenDto.refreshToken(), member.getId(),
                member.getEmail(), member.getNickName(), authentication.getAuthorities().toString());

        updateRedisToken(redisKey, tokenDto.refreshToken(), tokenProvider.getExpiration(tokenDto.refreshToken()));

        return buildResponse(memberInfoDto);
    }

    private ResponseEntity<MemberInfoDto> buildResponse(MemberInfoDto memberInfoDto) {
        ResponseCookie cookie = createResponseCookie(memberInfoDto.refreshToken());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SET_COOKIE, cookie.toString());

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(memberInfoDto);
    }

    private ResponseCookie createResponseCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN.getType(), refreshToken)
                .maxAge(oAuth2Properties.refreshTokenValidityInMilliseconds())
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
    }
    private void updateRedisToken(String key, String value, long expiration) {
        redisTemplate.opsForValue().set(key, value, expiration, TimeUnit.MILLISECONDS);
    }

    private ResponseCookie createLogOutCookie() {
        return ResponseCookie.from(REFRESH_TOKEN.getType(), "")
                .maxAge(1)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
    }

}
