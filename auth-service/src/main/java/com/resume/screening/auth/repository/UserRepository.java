package com.resume.screening.auth.repository;

import com.resume.screening.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    ArrayList<User> findByRole(String role);

    Boolean existsByEmail(String email);

}
