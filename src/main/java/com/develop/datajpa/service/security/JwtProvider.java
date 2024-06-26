package com.develop.datajpa.service.security;

import com.develop.datajpa.dto.user.LoginInfo;
import com.develop.datajpa.entity.User;
import io.jsonwebtoken.Claims;
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
@RequiredArgsConstructor
@Component
public class JwtProvider {

    @Value("${jwt.secret.salt}")
    private String salt;

    private final static String SECRET_KEY = "zena.data.jpa.token.test";

    private final Long exp = 1000L * 60 * 60;   // 만료시간 : 1Hour

    public String createToken(User user) {
        log.info("salt = {}", salt);  // 이거 어디씀??

        return Jwts.builder()
            // TODO 확인 후 필요없으면 지우기
//            .setIssuedAt(new Date(System.currentTimeMillis()))
            .claim("userId", user.getUserId())
            .setExpiration(new Date(System.currentTimeMillis() + exp))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }

    // Authorization Header를 통해 인증을 한다.
    public static LoginInfo resolveToken(String token) {

        log.info("token, in doFilterInternal = {}", token);

        if (token != null && validateToken(token)) {
            token = token.split(" ")[1].trim();
            return resolveLoginInfoFromJwt(token);
        }

        throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "로그인 정보가 확인되지 않습니다.");

    }

    public static LoginInfo resolveLoginInfoFromJwt(String jwtToken) {
        Claims claims = parseClaims(jwtToken);

        log.info("claims = {}", claims);

        if (isNull(claims)) {
            throw new HttpClientErrorException(HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
        }

        return LoginInfo.builder()
            .userId(claims.get("userId", String.class))
            .build();
    }

    // 토큰 검증
    public static boolean validateToken(String token) {
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

    private static Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
        }
    }
}
