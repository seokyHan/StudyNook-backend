package com.studyNook.global.Jwt.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("exclude")
public record ExcludeProperties(String[] path) {
}
