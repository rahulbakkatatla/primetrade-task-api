package com.primetrade.service;

import com.primetrade.dto.Dto.*;
import com.primetrade.entity.User;
import com.primetrade.repository.UserRepository;
import com.primetrade.security.JwtUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService) {
        this.userRepository = userRepository; this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil; this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new IllegalArgumentException("Username already taken");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Email already registered");
        User user = User.builder().username(request.getUsername()).email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())).role(User.Role.ROLE_USER).build();
        userRepository.save(user);
        UserDetails ud = userDetailsService.loadUserByUsername(user.getUsername());
        return AuthResponse.builder().token(jwtUtil.generateToken(ud))
                .username(user.getUsername()).role(user.getRole().name()).message("Registration successful").build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails ud = userDetailsService.loadUserByUsername(request.getUsername());
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        return AuthResponse.builder().token(jwtUtil.generateToken(ud))
                .username(user.getUsername()).role(user.getRole().name()).message("Login successful").build();
    }
}
