package com.example.task_management.common.config;

import com.example.task_management.auth.dto.AuthenticationResponse;
import com.example.task_management.auth.dto.CustomUserDetails;
import com.example.task_management.common.exceptions.BadRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.example.task_management.common.config.JwtRequestFilter.TOKEN_TYPE;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final ObjectMapper objectMapper;

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long expirationTimeHrs;

    public CustomUserDetails extractUserDetails(String token) {
        try {
            String subject = extractClaim(token, Claims::getSubject);
            return objectMapper.readValue(subject, CustomUserDetails.class);
        } catch (JsonProcessingException | JwtException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public AuthenticationResponse generateToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails);
    }

    private AuthenticationResponse createToken(Map<String, Object> claims, CustomUserDetails userDetails) {
        LocalDateTime expiration = LocalDateTime.now().plusHours(expirationTimeHrs);
        String subject;
        try {
            subject = objectMapper.writeValueAsString(userDetails);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
        String token = Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(getKey())
                .compact();

        return new AuthenticationResponse(TOKEN_TYPE + token, TOKEN_TYPE, expiration);
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public boolean validateToken(String token) {
        final CustomUserDetails tokenUserDetails = extractUserDetails(token);
        return (tokenUserDetails != null && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
