package com.develop.datajpa.config;

import com.develop.core.security.jwt.JwtAccessDeniedHandler;
import com.develop.core.security.jwt.JwtAuthenticationEntryPoint;
import com.develop.core.security.jwt.JwtAuthenticationFilter;
import com.develop.core.security.jwt.JwtExceptionFilter;
import com.develop.core.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authz) -> {
                    authz
                        .requestMatchers(HttpMethod.POST, "/article/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/article/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/article/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/shop/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/shop/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH,"/shop/**").authenticated()

                        .requestMatchers("/mypage/**").authenticated()

                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().permitAll();
                }
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .exceptionHandling(e -> e.accessDeniedHandler(jwtAccessDeniedHandler))
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }
}
