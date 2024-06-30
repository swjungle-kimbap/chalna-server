package com.jungle.chalnaServer.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jungle.chalnaServer.global.auth.jwt.CustomUserDetailsService;
import com.jungle.chalnaServer.global.auth.jwt.filter.JwtAuthFilter;
import com.jungle.chalnaServer.global.auth.jwt.handler.JwtAccessDeniedHandler;
import com.jungle.chalnaServer.global.auth.jwt.handler.JwtAuthDeniedHandler;
import com.jungle.chalnaServer.global.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthDeniedHandler jwtAuthDeniedHandler;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper;

    private static final String[] AUTHENTICATE_WHITELIST = {
            "/api/v1/auth/**","/images/**","/uploads/**","/health-check", "/api/ws/**","/api/v2/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(CorsConfigurer::disable)
                .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.formLogin(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorize ->
            authorize
                    .requestMatchers(AUTHENTICATE_WHITELIST).permitAll()
                    .anyRequest().authenticated()
        );
        http.addFilterBefore(new JwtAuthFilter(jwtService,customUserDetailsService, objectMapper), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(exceptionHandle -> {
            exceptionHandle.accessDeniedHandler(jwtAccessDeniedHandler);
            exceptionHandle.authenticationEntryPoint(jwtAuthDeniedHandler);
        });
        return http.build();
    }
}
