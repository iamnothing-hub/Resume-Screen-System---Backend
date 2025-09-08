package com.resume.screening.auth.service.impl;

import com.resume.screening.auth.dto.UserDto;
import com.resume.screening.auth.entity.RefreshToken;
import com.resume.screening.auth.entity.User;
import com.resume.screening.auth.exception.UserAlreadyExistException;
import com.resume.screening.auth.exception.UserNotFoundException;
import com.resume.screening.auth.jwt.JWTService;
import com.resume.screening.auth.repository.RefreshTokenRepository;
import com.resume.screening.auth.repository.UserRepository;
import com.resume.screening.auth.service.AuthService;
import com.resume.screening.auth.service.UserService;
import com.resume.screening.auth.util.TokenHashUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

@Service(value = "userService")
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ModelMapper mapper;
    
    private final UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder;

    private final JWTService jwtService;

    private final AuthService authService;

    private final UserDetailsService userDetailsService;



    // Constructor-based dependency injection
   /* public UserServiceImpl(ModelMapper mapper, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }*/


    //todo: find device ID by HttpServletRequest
    // helper to read deviceId header (else fallback)
    private String deviceId(HttpServletRequest req) {
        String d = req.getHeader("X-Device-Id");
        return (d == null || d.isBlank()) ? "unknown-device" : d;
    }


    //todo: Register user
    @Override
    public UserDto createUser(UserDto userDto) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistException("User is already exist with email: " + userDto.getEmail());
        }

        userDto.setPassword(this.passwordEncoder.encode(userDto.getPassword()));
        User save = userRepository.save(this.mapper.map(userDto, User.class));
        save.setPassword(null);
        return this.mapper.map(save, UserDto.class);
    }

    //todo: Login user
    @Override
    public Map<String, String> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User is not found with email: " + email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");

        }

        // find role by user details service
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String role = userDetails.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse("ROLE_USER");


        Map<String, String> tokens = authService.issueTokens(email, role, "Unknown-Device");

        return tokens;

    }


    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User is not found with email: " + email));
        user.setPassword(null);
        return this.mapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User is not found with id: " + id));
        user.setPassword(null);
        return mapper.map(user, UserDto.class);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);

    }

    @Override
    public ArrayList<UserDto> getUserByRole(String role) {

        ArrayList<User> users = userRepository.findByRole(role);
        ArrayList<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            user.setPassword(null);
            userDtos.add(mapper.map(user, UserDto.class));
        }
        return userDtos;
    }
}
