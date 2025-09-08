package com.resume.screening.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JWTService {

    @Value("${jwt.token.secret.key}")
    private String SECRET_KEY;

    @Value("${jwt.token.expiration.time}")
    private long ACCESS_TOKEN_VALIDITY;

    @Value("${jwt.token.refresh.time}")
    private long REFRESH_TOKEN_VALIDITY;



    public SecretKey key() {
        // HS256 key
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }


    public String generateToken(String email, String role){
        System.out.println(" JWTService Class -> Secret key: " + SECRET_KEY);
        System.out.println(" JWTService Class -> Token validity: " + ACCESS_TOKEN_VALIDITY);

        String generatedToken  = Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();

        System.out.println(" JWTService Class -> Generated Token: " + generatedToken);
        return generatedToken;

    }

    public String generateRefreshToken(String email, String deviceId){
        String refreshToken  = Jwts.builder()
                .subject(email)
                .claim("deviceId", deviceId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();

        System.out.println(" JWTService Class -> Generated Refresh Token: " + refreshToken);
        return refreshToken;
    }

    public Claims validateToken(String token){
        Claims payload = Jwts.parser()
//                .setSigningKey(SECRET_KEY.getBytes())
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        System.out.println(" JWTService Class -> Token payload: " + payload);
        return payload;

    }

}
