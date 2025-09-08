package com.resume.screening.auth.jwt;

import com.resume.screening.auth.entity.User;
import com.resume.screening.auth.exception.UserNotFoundException;
import com.resume.screening.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        System.out.println("JWTAuthFilter -> Authorization Header: " + header);
        if (header == null || !header.startsWith("Bearer ")) {
//            System.out.println("JWTAuthFilter -> request: "+request);
//            System.out.println("JWTAuthFilter -> response: "+response);
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
//        System.out.println("JWTAuthFilter -> Token: " + token);
        try{
            Claims claims = jwtService.validateToken(token);
            System.out.println("JWTAuthFilter -> Claims: " + claims);
            String email = claims.getSubject();
            System.out.println("JWTAuthFilter -> Email from Claims: " + email);


            User user1 = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User is not found with email " + email));

            System.out.println("JWTAuthFilter -> User from DB: " + user1);

            UserDetails userDetails = new UserDetails() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return List.of(new SimpleGrantedAuthority(user1.getRole()));
                }

                @Override
                public String getPassword() {
                    return user1.getPassword();
                }

                @Override
                public String getUsername() {
                    return user1.getEmail();
                }
            };
        /*UserDetails user = userRepository.findByEmail(email)
                .map(u -> new org.springframework.security.core.userdetails.User(
                        u.getEmail(), u.getPassword(),
                        List.of(new SimpleGrantedAuthority(u.getRole()))))
                .orElse(null);
*/
            System.out.println("JWTAuthFilter -> UserDetails: " + userDetails);


            if (userDetails != null) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                System.out.println("JWTAuthFilter -> Auth: " + auth);

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        catch (JwtException e){
            throw new JwtException("JWT token is invalid");
        }
        chain.doFilter(request, response);
    }
}
