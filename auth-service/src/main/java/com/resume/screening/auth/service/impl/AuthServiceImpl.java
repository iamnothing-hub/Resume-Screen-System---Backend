package com.resume.screening.auth.service.impl;

import com.resume.screening.auth.entity.RefreshToken;
import com.resume.screening.auth.jwt.JWTService;
import com.resume.screening.auth.repository.RefreshTokenRepository;
import com.resume.screening.auth.service.AuthService;
import com.resume.screening.auth.util.TokenHashUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JWTService jwtService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final TokenHashUtil tokenHashUtil;


    @Override
    // called on login after credentials verified
    public Map<String, String> issueTokens(String email, String role, String deviceId) {
        String access = jwtService.generateToken(email, role);
        String refresh = jwtService.generateRefreshToken(email, deviceId);

        RefreshToken rt = new RefreshToken();
        rt.setUserEmail(email);
        rt.setDeviceId(deviceId);
        rt.setTokenHash(tokenHashUtil.sha256(refresh));
        rt.setExpiresAt(Instant.ofEpochMilli(Jwts.parser().verifyWith(jwtService.key()).build()
                .parseSignedClaims(refresh).getPayload().getExpiration().getTime()));
        refreshTokenRepository.save(rt);

        return Map.of("accessToken", access, "refreshToken", refresh);
    }

    // refresh endpoint → rotate token (old revoke + new issue)
    @Override
    public Map<String, String> rotateRefreshToken(String oldRefreshToken) {
        Claims claims = jwtService.validateToken(oldRefreshToken);
        String email = claims.getSubject();
        String deviceId = claims.get("device", String.class);

        String oldHash = tokenHashUtil.sha256(oldRefreshToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHashAndRevokedFalse(oldHash)
                .orElseThrow(() -> new RuntimeException("Invalid/Revoked refresh token"));

        if (stored.getExpiresAt().isBefore(Instant.now()))
            throw new RuntimeException("Refresh token expired");

        // revoke old
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        // issue new pair
        String access = jwtService.generateToken(email, "USER"); // role lookup if needed
        String newRefresh = jwtService.generateRefreshToken(email, deviceId);

        RefreshToken newRt = new RefreshToken();
        newRt.setUserEmail(email);
        newRt.setDeviceId(deviceId);
        newRt.setTokenHash(tokenHashUtil.sha256(newRefresh));
        newRt.setExpiresAt(Instant.ofEpochMilli(
                Jwts.parser().verifyWith(jwtService.key()).build().parseSignedClaims(newRefresh)
                        .getPayload().getExpiration().getTime()));
        refreshTokenRepository.save(newRt);

        return Map.of("accessToken", access, "refreshToken", newRefresh);
    }

    @Override
    public void logoutDevice(String email, String deviceId) {
        refreshTokenRepository.revokeByUserAndDevice(email, deviceId);
    }

    @Override
    public void logoutAllDevices(String email) {
        refreshTokenRepository.revokeAllForUser(email);
    }
}
