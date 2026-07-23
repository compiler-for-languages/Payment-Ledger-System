package com.infotact.project1.controller;

import com.infotact.project1.dto.request.ChangePasswordRequestDTO;
import com.infotact.project1.dto.request.UpdateProfileRequestDTO;
import com.infotact.project1.dto.response.UserResponseDTO;
import com.infotact.project1.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get authenticated user profile")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getUsername()));
    }

    @Operation(summary = "Update authenticated user profile")
    @PatchMapping("/me")
    public ResponseEntity<UserResponseDTO> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                         @Valid @RequestBody UpdateProfileRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.updateProfile(userDetails.getUsername(), requestDTO));
    }

    @Operation(summary = "Change authenticated user password")
    @PatchMapping("/me/password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                 @Valid @RequestBody ChangePasswordRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.changePassword(userDetails.getUsername(), requestDTO));
    }
}
