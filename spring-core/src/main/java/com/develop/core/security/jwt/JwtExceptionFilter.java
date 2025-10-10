package com.develop.core.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, "로그인이 만료되었습니다");
        } catch (SignatureException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, "인증 오류 (Invalid token signature)");
        } catch (JwtException e) {
            setErrorResponse(HttpStatus.FORBIDDEN, response, "인증 오류 (Invalid token)");
        } catch (Exception e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, String.format("인증 오류 (%s)", e.getMessage()));
        }
    }

    private void setErrorResponse(HttpStatus status,
                                  HttpServletResponse response,
                                  String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("message", message);

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
