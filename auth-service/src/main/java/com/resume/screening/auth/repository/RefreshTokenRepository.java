package com.resume.screening.auth.repository;

import com.resume.screening.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);

    @Modifying
    @Query("update RefreshToken r set r.revoked = true where r.userEmail = :email and r.deviceId = :deviceId and r.revoked = false")
    int revokeByUserAndDevice(@Param("email") String email, @Param("deviceId") String deviceId);

    @Modifying
    @Query("update RefreshToken r set r.revoked = true where r.userEmail = :email and r.revoked = false")
    int revokeAllForUser(@Param("email") String email);
}
