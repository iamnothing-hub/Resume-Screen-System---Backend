package com.resume.screening.auth.service;

import java.util.Map;

public interface AuthService {

    Map<String, String> issueTokens(String email, String role, String deviceId);

    Map<String, String> rotateRefreshToken(String oldRefreshToken);

    void logoutDevice(String email, String deviceId);

    void logoutAllDevices(String email);
}
