package com.studyNook.global.security.jwt.filter;

import com.studyNook.global.security.jwt.TokenProvider;
import com.studyNook.global.security.jwt.props.ExcludeProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final ExcludeProperties excludeProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();

        if(!isWhitePath(contextPath, requestUri)) {
            String token = tokenProvider.resolveToken(request);
            if(!tokenProvider.validateToken(token)) return;
            userAuthFilter(token);
        }

        filterChain.doFilter(request, response);
    }

    private void userAuthFilter(String token) {
        Authentication authentication = tokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    private boolean isWhitePath(String contextPath, String requestUri) {
        return Arrays.stream(excludeProperties.path())
                .anyMatch(req -> equalsIgnoreCase(requestUri, format("%s%s", contextPath, req)));
    }
}
