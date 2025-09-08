package com.resume.screening.auth.service;

import com.resume.screening.auth.dto.UserDto;

import java.util.ArrayList;
import java.util.Map;

public interface UserService {

    UserDto createUser(UserDto userDto);
    Map<String, String> login(String email, String password);
    UserDto getUserByEmail(String email);
    UserDto getUserById(Long id);
    void deleteUser(Long id);
    ArrayList<UserDto> getUserByRole(String role);
}
