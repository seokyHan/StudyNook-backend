package com.studyNook.oauth2.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("oauth2")
public record OAuth2Properties(String redirectUrl,
                               long accessTokenValidityInMilliseconds,
                               long refreshTokenValidityInMilliseconds) {
}
