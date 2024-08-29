package com.studyNook.global.common.config;

import com.studyNook.global.security.jwt.props.ExcludeProperties;
import com.studyNook.global.security.jwt.props.JwtProperties;
import com.studyNook.global.common.props.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {
        RedisProperties.class,
        JwtProperties.class,
        ExcludeProperties.class
})
public class PropertiesConfiguration {
}
