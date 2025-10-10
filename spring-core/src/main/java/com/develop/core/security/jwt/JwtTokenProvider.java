package com.develop.core.security.jwt;

import com.develop.core.security.dto.LoginInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Date;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private final Long exp = 1000L * 60 * 60;

    public String createToken(String userId, String userNickname, String userName, String role) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("userNickname", userNickname)
                .claim("userName", userName)
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + exp))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public LoginInfo resolveToken(String token) {
        if (token != null && validateToken(token)) {
            token = token.split(" ")[1].trim();
            return resolveLoginInfoFromJwt(token);
        }

        throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "로그인 정보가 확인되지 않습니다.");

    }

    public LoginInfo resolveLoginInfoFromJwt(String jwtToken) {
        Claims claims = parseClaims(jwtToken);

        if (isNull(claims)) {
            throw new HttpClientErrorException(HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
        }

        return LoginInfo.builder()
                .userId(claims.get("userId", String.class))
                .userNickname(claims.get("userNickname", String.class))
                .userName(claims.get("userName", String.class))
                .role(claims.get("role", String.class))
                .build();
    }

    public boolean validateToken(String token) {
        try {
            if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
                return false;
            } else {
                token = token.split(" ")[1].trim();
            }

            Claims claim = parseClaims(token);
            return !claim.getExpiration().before(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
        }
    }

    public String generateToken(String tempId) {
        return Jwts.builder()
                .claim("userId", tempId)
                .setExpiration(new Date(System.currentTimeMillis() + exp))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
