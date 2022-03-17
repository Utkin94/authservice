package com.interview.authservice.inboun.http;

import com.interview.authservice.inboun.http.advice.ApiError;
import com.interview.authservice.inboun.http.model.JwtTokenPairDto;
import com.interview.authservice.inboun.http.model.LoginRequest;
import com.interview.authservice.inboun.http.model.TokenRefreshRequest;
import com.interview.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "User Login")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully Logged-in",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = JwtTokenPairDto.class))}),
            @ApiResponse(
                    responseCode = "403",
                    description = "Wrong username/password pair"),
            @ApiResponse(
                    responseCode = "400",
                    description = "User not exists",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
    })
    @PostMapping("/login")
    public JwtTokenPairDto login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }


    @Operation(summary = "Refresh user tokens by refresh token")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully Refreshed",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = JwtTokenPairDto.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid refresh token",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
    })
    @PostMapping("/refresh")
    public JwtTokenPairDto refresh(@Valid @RequestBody TokenRefreshRequest refreshRequest) {
        return authService.refresh(refreshRequest);
    }
}
