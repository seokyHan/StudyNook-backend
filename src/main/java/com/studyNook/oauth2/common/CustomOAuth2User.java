package com.studyNook.oauth2.common;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private String email;
    private boolean isFirstLogin;


    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            String email,
                            boolean isFirstLogin) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.isFirstLogin = isFirstLogin;
    }
}
