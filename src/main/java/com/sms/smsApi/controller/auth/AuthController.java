package com.sms.smsApi.controller.auth;

import com.sms.smsApi.dto.LoginDto;
import com.sms.smsApi.dto.RegistrationDto;
import com.sms.smsApi.dto.VerifyUserDto;
import com.sms.smsApi.exception.AccountDisabledException;
import com.sms.smsApi.exception.AccountNotVerifiedException;
import com.sms.smsApi.exception.InvalidCredentialsException;
import com.sms.smsApi.exception.VerificationCodeExpiredException;
import com.sms.smsApi.model.User;
import com.sms.smsApi.reponse.ApiResponse;
import com.sms.smsApi.reponse.LoginResponse;
import com.sms.smsApi.service.AuthService.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

//    @PostMapping("/signup")
//    public ResponseEntity<?> register(@RequestBody RegistrationDto registerUserDto) {
//        try {
//            LOGGER.info("Registration attempt for email: {}", registerUserDto.getEmail());
//            User registeredUser = authService.signup(registerUserDto);
//            LOGGER.info("User registered successfully: {}", registeredUser.getEmail());
//
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "Registration successful. Please check your email for verification code.");
//            response.put("email", registeredUser.getEmail());
//
//            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//        } catch (RuntimeException e) {
//            LOGGER.error("Registration failed: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> authenticate(@RequestBody LoginDto loginUserDto, HttpServletResponse response) throws InvalidCredentialsException, AccountNotVerifiedException, AccountDisabledException {
        try {
            LOGGER.info("Login attempt for email: {}", loginUserDto.getEmail());
            LoginResponse loginResponse = authService.authenticate(loginUserDto, response);
            LOGGER.info("User authenticated successfully: {}", loginUserDto.getEmail());
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Login Successfully!",
                            loginResponse
                    )
            );
        } catch (RuntimeException e) {
            LOGGER.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(
                            false,
                            "Invalid user!",
                            null
                    )
            );
        } catch (InvalidCredentialsException e) {
            throw new InvalidCredentialsException(e);
        } catch (AccountNotVerifiedException e) {
            throw new AccountNotVerifiedException(e);
        } catch (AccountDisabledException e) {
            throw new AccountDisabledException(e);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto,  HttpServletResponse responseServer) throws VerificationCodeExpiredException {
        try {
            LOGGER.info("Verification attempt for email: {}", verifyUserDto.getEmail());
            LoginResponse response = authService.verifyAndLogin(verifyUserDto, responseServer);

            LOGGER.info("User verified successfully: {}", verifyUserDto.getEmail());
            response.setMessage("Account verified successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            LOGGER.error("Verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (InvalidCredentialsException e) {
            throw new RuntimeException(e);
        } catch (VerificationCodeExpiredException e) {
            throw new VerificationCodeExpiredException(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            LOGGER.info("Resend verification code request for email: {}", email);
            authService.resendVerificationCode(email);
            LOGGER.info("Verification code resent successfully to: {}", email);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Verification code sent successfully");
            response.put("email", email);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            LOGGER.error("Resend verification code failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        authService.refreshToken(request,response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ){
        try {
            boolean isLogout =authService.logout(request, response, authentication);

            if(!isLogout){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new ApiResponse<>(
                                false,
                                "No Token Provided!",
                                null
                        )
                );
            }

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Login Successfully!",
                            null
                    )
            );
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(
                            false,
                            "Internal Error:" + e.getMessage(),
                            null
                    )
            );
        }
    }

    // Helper method to create consistent error responses
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}