package com.develop.datajpa.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfiguration {

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authz) -> {
                authz
                    .requestMatchers("/register", "/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/admin/**").authenticated()
                    .requestMatchers(HttpMethod.POST, "/user/**").authenticated()
                    .requestMatchers("/my/**").authenticated()
                    .anyRequest().denyAll();
            }).httpBasic(withDefaults());
        // TODO : JWT 인증 필터 적용? 뭔지 다시 한번 보기 이제 안써도 되는듯??
//            .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }
}
