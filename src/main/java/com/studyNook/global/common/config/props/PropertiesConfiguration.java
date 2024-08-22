package com.studyNook.global.common.config.props;

import com.studyNook.global.common.config.redis.props.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {
        RedisProperties.class
})
public class PropertiesConfiguration {
}
