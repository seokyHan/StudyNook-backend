package com.studyNook.global.security.jwt.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("exclude")
public record ExcludeProperties(String[] path) {
}
