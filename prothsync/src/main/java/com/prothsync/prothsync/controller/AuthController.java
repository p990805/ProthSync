package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.dto.SignupRequestDTO;
import com.prothsync.prothsync.dto.SignupResponseDTO;
import com.prothsync.prothsync.entity.user.User;
import com.prothsync.prothsync.service.AuthService;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URL;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@Valid @RequestBody SignupRequestDTO signupRequest){

        User user = authService.signup(signupRequest);
        return ResponseEntity.ok(SignupResponseDTO.from(user));
    }

}
