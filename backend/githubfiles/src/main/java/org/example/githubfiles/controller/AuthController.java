package org.example.githubfiles.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.githubfiles.dto.ApiResponseDto;
import org.example.githubfiles.exception.notfound.UserNotFoundException;
import org.example.githubfiles.exception.unauthorized.InvalidRefreshTokenException;
import org.springframework.security.core.Authentication;
import org.example.githubfiles.dto.AuthRequestDto;
import org.example.githubfiles.model.AppUser;
import org.example.githubfiles.model.RefreshToken;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.example.githubfiles.security.jwt.JwtUtil;
import org.example.githubfiles.service.AppUserService;
import org.example.githubfiles.service.RefreshTokenService;
import java.time.Duration;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final AppUserService userService;
    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder passwordEncoder;

    @Operation(
            summary = "Login with username and password",
            description = "Authenticates a user and returns access & refresh tokens as HTTP-only cookies.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Login successful, access and refresh tokens are set in cookies"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid credentials",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = @ExampleObject(name = "Invalid Credentials", value = """
                    {
                        "timestamp": "2025-07-29T10:10:10.000Z",
                        "status": 401,
                        "success": false,
                        "message": "Geçersiz kullanıcı adı veya şifre"
                    }
                """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = @ExampleObject(name = "User Not Found", value = """
                    {
                        "timestamp": "2025-07-29T10:10:20.000Z",
                        "status": 404,
                        "success": false,
                        "message": "Kullanıcı bulunamadı"
                    }
                """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = @ExampleObject(name = "Unexpected Error", value = """
                    {
                        "timestamp": "2025-07-29T10:10:30.000Z",
                        "status": 500,
                        "success": false,
                        "message": "Beklenmeyen bir hata oluştu"
                    }
                """)
                            )
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody AuthRequestDto request, HttpServletResponse response) {

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        AppUser user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı"));

        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofMinutes(10))
                .secure(false)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .secure(false)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Logout the current user",
            description = "Clears access and refresh token cookies. If a refresh token exists in cookies, it is deleted from the database. Always returns 204 regardless of presence of token.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Logout successful. Cookies cleared."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - Something went wrong during logout.",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = @ExampleObject(value = """
                    {
                        "timestamp": "2025-07-29T12:00:10.000Z",
                        "status": 500,
                        "success": false,
                        "message": "An unexpected error occurred during logout."
                    }
                """)
                            )
                    )
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractTokenFromCookie(request, "refreshToken");

        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.deleteByToken(refreshToken);
        }

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", refreshCookie.toString());
        response.addHeader("Set-Cookie", accessCookie.toString());

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Refresh access token",
            description = "Validates the refresh token in cookies. If valid, issues a new access token and sets it in an HttpOnly cookie.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Access token refreshed successfully. New token set in cookie."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Refresh token is missing, expired, or invalid",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = @ExampleObject(name = "Invalid Refresh Token", value = """
                    {
                        "timestamp": "2025-07-29T13:15:00.000Z",
                        "status": 401,
                        "success": false,
                        "message": "Geçersiz refresh token"
                    }
                """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Unexpected server error",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseDto.class),
                                    examples = @ExampleObject(name = "Unexpected Error", value = """
                    {
                        "timestamp": "2025-07-29T13:15:10.000Z",
                        "status": 500,
                        "success": false,
                        "message": "Beklenmeyen bir hata oluştu"
                    }
                """)
                            )
                    )
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getCookieValue(request, "refreshToken");

        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .filter(refreshTokenService::isValid)
                .orElseThrow(() -> new InvalidRefreshTokenException("Geçersiz refresh token"));

        AppUser user = token.getUser();
        String newAccessToken = jwtUtil.generateToken(user);

        ResponseCookie newAccessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofMinutes(10))
                .build();

        response.addHeader("Set-Cookie", newAccessCookie.toString());
        return ResponseEntity.noContent().build();
    }

    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}