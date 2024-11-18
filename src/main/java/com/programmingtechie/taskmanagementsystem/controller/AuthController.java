package com.programmingtechie.taskmanagementsystem.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.programmingtechie.taskmanagementsystem.dto.AuthenticationDTO;
import com.programmingtechie.taskmanagementsystem.dto.RefreshTokenDTO;
import com.programmingtechie.taskmanagementsystem.mapper.UserMapper;
import com.programmingtechie.taskmanagementsystem.model.UserImpl;
import com.programmingtechie.taskmanagementsystem.service.UserService;
import com.programmingtechie.taskmanagementsystem.util.JWTUtil;
import com.programmingtechie.taskmanagementsystem.util.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    @PostMapping("/register")
    public Map<String, String> performRegistration(@RequestBody AuthenticationDTO authenticationDTO,
                                                   BindingResult bindingResult) {
        UserImpl userImpl = userMapper.convertToUserImpl(authenticationDTO);
        userValidator.validate(userImpl, bindingResult);
        if (bindingResult.hasErrors()) {
            return Map.of("message", "Validation error");
        }

        userService.registerUser(userImpl);

        String accessToken = jwtUtil.generateAccessToken(userImpl.getEmail(), userImpl.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(userImpl.getEmail());

        return Map.of("access-token", accessToken, "refresh-token", refreshToken);
    }

    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getEmail(),
                        authenticationDTO.getPassword());
        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            return Map.of("message", "Invalid email or password");
        }

        Optional<UserImpl> user = userService.findByEmail(authenticationDTO.getEmail());
        if (user.isEmpty()) {
            return Map.of("message", "User not found");
        }

        String accessToken = jwtUtil.generateAccessToken(user.get().getEmail(), user.get().getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.get().getEmail());

        return Map.of("access-token", accessToken, "refresh-token", refreshToken);
    }

    @PostMapping("/refresh-token")
    public Map<String, String> refreshAccessToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        try {
            String email = jwtUtil.validateTokenAndRetrieveClaim(refreshTokenDTO.getRefreshToken(), "refresh");
            Optional<UserImpl> user = userService.findByEmail(email);

            if (user.isPresent()) {
                String newAccessToken = jwtUtil.generateAccessToken(email, user.get().getRole());
                return Map.of("access-token", newAccessToken);
            } else {
                return Map.of("message", "User not found");
            }

        } catch (JWTVerificationException e) {
            return Map.of("message", "Invalid refresh token");
        }
    }
}
