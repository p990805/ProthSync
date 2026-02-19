package com.prothsync.prothsync.controller;

import com.prothsync.prothsync.controller.docs.UserControllerDocs;
import com.prothsync.prothsync.dto.MyProfileResponseDTO;
import com.prothsync.prothsync.dto.NearbyUserResponseDTO;
import com.prothsync.prothsync.dto.ProfileUpdateRequestDTO;
import com.prothsync.prothsync.dto.UserProfileResponseDTO;
import com.prothsync.prothsync.entity.user.UserType;
import com.prothsync.prothsync.security.CustomUserDetails;
import com.prothsync.prothsync.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserControllerDocs {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<MyProfileResponseDTO> getMyProfile(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        MyProfileResponseDTO response = userService.getMyProfile(userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(
        @PathVariable Long userId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserProfileResponseDTO response = userService.getUserProfile(
            userId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<MyProfileResponseDTO> updateProfile(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody ProfileUpdateRequestDTO request
    ) {
        MyProfileResponseDTO response = userService.updateProfile(
            userDetails.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyUserResponseDTO>> getNearbyUsers(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(required = false) Double radiusKm,
        @RequestParam(required = false) Integer limit
    ) {
        List<NearbyUserResponseDTO> response = userService.findNearbyUsers(
            userDetails.getUserId(), radiusKm, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nearby/{userType}")
    public ResponseEntity<List<NearbyUserResponseDTO>> getNearbyUsersByType(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable UserType userType,
        @RequestParam(required = false) Double radiusKm,
        @RequestParam(required = false) Integer limit
    ) {
        List<NearbyUserResponseDTO> response = userService.findNearbyUsersByType(
            userDetails.getUserId(), userType, radiusKm, limit);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/geocode")
    public ResponseEntity<Void> retryGeocoding(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userService.retryGeocoding(userDetails.getUserId());
        return ResponseEntity.ok().build();
    }
}