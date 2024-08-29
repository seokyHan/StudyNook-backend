package com.studyNook.global.security.config;

import com.studyNook.global.security.jwt.TokenProvider;
import com.studyNook.global.security.jwt.filter.JwtAuthenticationFilter;
import com.studyNook.global.security.jwt.handler.CustomAccessDeniedHandler;
import com.studyNook.global.security.jwt.handler.EntryPointUnauthorizedHandler;
import com.studyNook.global.security.jwt.props.ExcludeProperties;
import com.studyNook.global.common.filter.ExceptionHandlerFilter;
import com.studyNook.global.common.filter.log.LoggingFilter;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
@RequiredArgsConstructor
public class WebSecurityConfigure {
    private final TokenProvider tokenProvider;
    private final EntryPointUnauthorizedHandler unauthorizedHandler;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final LoggingFilter loggingFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final ExcludeProperties excludeProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Authorization", "Origin, Accept", "X-Requested-With", "Set-Cookie",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setMaxAge(6000L);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain config(HttpSecurity httpSecurity) throws Exception {
        AuthorityAuthorizationManager<RequestAuthorizationContext> admin = AuthorityAuthorizationManager.hasAnyAuthority("ROLE_ADMIN");
        AuthorityAuthorizationManager<RequestAuthorizationContext> user = AuthorityAuthorizationManager.hasAnyAuthority("ROLE_USER");
        AuthorityAuthorizationManager<RequestAuthorizationContext> guest = AuthorityAuthorizationManager.hasAnyAuthority("ROLE_GUEST");
        AntPathRequestMatcher[] antPathRequestMatchers = stream(excludeProperties.path()).map(AntPathRequestMatcher::antMatcher).toArray(AntPathRequestMatcher[]::new);

        return httpSecurity
                .cors(corsConfig ->
                        corsConfig.configurationSource(corsConfigurationSource())
                )
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(antPathRequestMatchers).permitAll()
                                .requestMatchers(getAdminMatcher()).access(admin)
                                .requestMatchers(getUserMatchers()).access(user)
                                .requestMatchers(getGuestMatchers()).access(guest)
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler(accessDeniedHandler)
                                .authenticationEntryPoint(unauthorizedHandler)
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, excludeProperties), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class)
                .addFilterBefore(loggingFilter, ExceptionHandlerFilter.class)
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring().requestMatchers("/images/**","/upload-dir/**","/h2-console/**","/favicon.ico");
    }

    @NotNull
    private RequestMatcher[] getUserMatchers() {
        return new RequestMatcher[]{
                antMatcher(POST, "/users/**"),
        };
    }

    @NotNull
    private RequestMatcher[] getAdminMatcher() {
        return new RequestMatcher[]{
                antMatcher(POST, "/users/**"),
        };
    }

    @NotNull
    private RequestMatcher[] getGuestMatchers() {
        return new RequestMatcher[]{
                antMatcher(POST, ""),
        };
    }
}
