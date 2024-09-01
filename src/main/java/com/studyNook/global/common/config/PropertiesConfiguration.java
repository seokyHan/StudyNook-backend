package com.studyNook.global.common.config;

import com.studyNook.global.security.jwt.props.ExcludeProperties;
import com.studyNook.global.security.jwt.props.JwtProperties;
import com.studyNook.global.common.props.RedisProperties;
import com.studyNook.oauth2.props.OAuth2Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {
        RedisProperties.class,
        JwtProperties.class,
        OAuth2Properties.class,
        ExcludeProperties.class,
})
public class PropertiesConfiguration {
}
