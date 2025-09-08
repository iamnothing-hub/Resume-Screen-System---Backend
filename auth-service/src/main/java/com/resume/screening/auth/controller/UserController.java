package com.resume.screening.auth.controller;

import com.resume.screening.auth.dto.UserDto;
import com.resume.screening.auth.payload.AppResponse;
import com.resume.screening.auth.service.AuthService;
import com.resume.screening.auth.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/auth/v1/users")
@Validated
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto){
        UserDto user = userService.createUser(userDto);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String email, @RequestParam String password) {
        return ResponseEntity.ok(userService.login(email, password));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        Map<String, String> tokens = authService.rotateRefreshToken(refreshToken);
        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id){
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserDto> findByEmail(@RequestParam @Email(message = "{EMAIL_NOT_VALID}") String email){
        UserDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/by-role")
    public ResponseEntity<ArrayList<UserDto>> findByRole(@RequestParam String role){
        ArrayList<UserDto> users = userService.getUserByRole(role);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AppResponse> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        AppResponse response = AppResponse.builder().message("User deleted successfully").success(true).status(HttpStatus.OK).timeStamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
