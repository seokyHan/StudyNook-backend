package com.studyNook.global.Jwt;

import com.studyNook.global.Jwt.props.JwtProperties;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Map;

@Slf4j
@Component
public class TokenProvider {
    private static final Map<String, Object> jwtHeader = Map.of("alg", "HS512", "typ", "JWT");
    private final long tokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final RedisTemplate redisTemplate;
    private final Key key;

    @Autowired
    public TokenProvider(JwtProperties properties, RedisTemplate redisTemplate) {
        this.tokenValidityInMilliseconds = properties.tokenValidityInMilliseconds();
        this.refreshTokenValidityInMilliseconds = properties.refreshTokenValidityInMilliseconds();
        byte[] keyBytes = Decoders.BASE64.decode(properties.secret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisTemplate = redisTemplate;
    }
}
