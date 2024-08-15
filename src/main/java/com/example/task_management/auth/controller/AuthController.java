package com.example.task_management.auth.controller;

import com.example.task_management.auth.dto.AuthenticationRequest;
import com.example.task_management.auth.dto.AuthenticationResponse;
import com.example.task_management.auth.dto.CustomUserDetails;
import com.example.task_management.auth.service.UserService;
import com.example.task_management.common.config.JwtUtil;
import com.example.task_management.common.exceptions.UnAuthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/token")
    public ResponseEntity<AuthenticationResponse> getToken(@RequestBody @Validated AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new UnAuthenticatedException("Incorrect username or password");
        }

        UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getEmail());
        CustomUserDetails customUserDetails = new CustomUserDetails(userDetails);
        customUserDetails.eraseCredentials();
        AuthenticationResponse authenticationResponse = jwtUtil.generateToken(customUserDetails);
        return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
    }
}

