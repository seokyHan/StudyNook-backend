package com.studyNook.global.Jwt.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
public record JwtProperties(String host,
                            String secret,
                            long tokenValidityInMilliseconds,
                            long refreshTokenValidityInMilliseconds) {
}
