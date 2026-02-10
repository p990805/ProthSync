package com.prothsync.prothsync.config;

import com.prothsync.prothsync.security.JwtAuthenticationEntryPoint;
import com.prothsync.prothsync.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .authorizeHttpRequests(auth -> auth
                // Auth
                .requestMatchers("/api/auth/signup", "/api/auth/login", "/api/auth/refresh").permitAll()
                // Swagger
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Posts - 공개 조회
                .requestMatchers(HttpMethod.GET, "/api/posts").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/posts/category/**").permitAll()
                // Comments - 공개 조회
                .requestMatchers(HttpMethod.GET, "/api/posts/*/comments").permitAll()
                // Hashtags - 공개 조회
                .requestMatchers(HttpMethod.GET, "/api/hashtags/**").permitAll()
                // Follow - 공개 조회
                .requestMatchers(HttpMethod.GET, "/api/users/*/follow/followers").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/*/follow/followings").permitAll()
                // 나머지는 인증 필요
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}