package com.adventurekm.backend.service;

import com.adventurekm.backend.config.JwtTokenProvider;
import com.adventurekm.backend.dto.request.LoginRequest;
import com.adventurekm.backend.dto.request.RegisterRequest;
import com.adventurekm.backend.dto.response.AuthResponse;
import com.adventurekm.backend.exception.BadRequestException;
import com.adventurekm.backend.model.User;
import com.adventurekm.backend.model.UserLevel;
import com.adventurekm.backend.repository.UserLevelRepository;
import com.adventurekm.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserLevelRepository userLevelRepository;
    private final InvitationService invitationService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        return new AuthResponse(
                jwtTokenProvider.generateAccessToken(request.username()),
                jwtTokenProvider.generateRefreshToken(request.username()));
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        invitationService.validateAndConsume(request.invitationToken());

        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();
        user = userRepository.save(user);

        UserLevel level = UserLevel.builder().user(user).build();
        userLevelRepository.save(level);

        return new AuthResponse(
                jwtTokenProvider.generateAccessToken(user.getUsername()),
                jwtTokenProvider.generateRefreshToken(user.getUsername()));
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        return new AuthResponse(
                jwtTokenProvider.generateAccessToken(username),
                jwtTokenProvider.generateRefreshToken(username));
    }
}
