package com.interview.authservice.inboun.http;

import com.interview.authservice.inboun.http.advice.ApiError;
import com.interview.authservice.inboun.http.model.UserCreationRequest;
import com.interview.authservice.inboun.http.model.UserDto;
import com.interview.authservice.inboun.http.model.UserUpdateRequest;
import com.interview.authservice.mapper.UserMapper;
import com.interview.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "User account creation")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "User already exists",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
    })
    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserCreationRequest request) {
        var createdUser = userService.createUser(request);
        return userMapper.mapUserEntityToDto(createdUser);
    }

    @Operation(summary = "User account updating")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(
                    responseCode = "403",
                    description = "Users can only be updated by their owners or admin"),
            @ApiResponse(
                    responseCode = "400",
                    description = "User not exists",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
    })
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or authentication.principal.equals(#userId.toString())")
    public UserDto updateUser(@Valid @RequestBody UserUpdateRequest request,
                              @PathVariable @Parameter(description = "User unique identifier") Long userId) {
        var user = userService.updateUser(userId, request);
        return userMapper.mapUserEntityToDto(user);
    }

    @Operation(summary = "User account removing")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User removed"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Users can only be removed by their owners or admin"),
            @ApiResponse(
                    responseCode = "400",
                    description = "User not exists",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))})
    })
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or authentication.principal.equals(#userId.toString())")
    public void removeUser(@PathVariable @Parameter(description = "User unique identifier") Long userId) {
        userService.removeUser(userId);
    }
}
