package com.resume.screening.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_rt_user", columnList = "userEmail"),
                @Index(name = "idx_rt_device", columnList = "deviceId"),
                @Index(name = "idx_rt_hash", columnList = "tokenHash", unique = true)
        })
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)           // email/user identifier
    private String userEmail;

    @Column(nullable = false)           // hashed(refreshToken)
    private String tokenHash;

    @Column(nullable = false)
    private String deviceId;            // client supplied; one per device/session

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // getters/setters
}

