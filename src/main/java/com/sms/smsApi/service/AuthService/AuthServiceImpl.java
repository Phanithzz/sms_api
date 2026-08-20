package com.sms.smsApi.service.AuthService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.smsApi.dto.LoginDto;
import com.sms.smsApi.dto.VerifyUserDto;
import com.sms.smsApi.exception.AccountDisabledException;
import com.sms.smsApi.exception.InvalidCredentialsException;
import com.sms.smsApi.exception.VerificationCodeExpiredException;
import com.sms.smsApi.model.*;
import com.sms.smsApi.reponse.AuthResponse;
import com.sms.smsApi.reponse.LoginResponse;
import com.sms.smsApi.repository.*;
import com.sms.smsApi.service.EmailService.EmailService;
import com.sms.smsApi.service.UserService.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    private static final String REFRESH_TOKEN_COOKIE_PATH = "/api/auth/refresh";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @Value("${security.jwt.refresh-expiration-ms}")
    private long refreshTokenExpiredTime;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final JdbcTemplate jdbcTemplate;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           EmailService emailService,
                           UserService userService,
                           TokenRepository tokenRepository,
                           JwtService jwtService, JdbcTemplate jdbcTemplate, StudentRepository studentRepository, TeacherRepository teacherRepository, ParentRepository parentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.userService = userService;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.jdbcTemplate = jdbcTemplate;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.parentRepository = parentRepository;
    }

    @Override
    public LoginResponse authenticate(LoginDto input, HttpServletResponse response)
            throws InvalidCredentialsException, AccountDisabledException {

        User user = userRepository.findByEmail(input.getEmail())
                .or(() -> userRepository.findByUsername(input.getUsername()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        if (!user.isEnabled()) {
            throw new AccountDisabledException("Account has been disabled.");
        }

        String authIdentifier = (input.getEmail() != null && !input.getEmail().isEmpty())
                ? input.getEmail()
                : user.getUsername();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authIdentifier, input.getPassword())
            );
        } catch (BadCredentialsException e) {
            LOGGER.error("Authentication failed for identifier: {}", authIdentifier);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.isVerified()) {
            sendNewVerificationCode(user);
            return LoginResponse.verificationRequired(
                    "Please verify your account. Verification code sent to your email.",
                    user.getEmail()
            );
        }

        return issueTokensAndBuildResponse(user, response);
    }
    private void handleUnverifiedUser(User user) {
        // Only generate new code if expired or doesn't exist
        if (user.getVerificationCode() == null ||
                user.getVerificationCodeExpireAt() == null ||
                user.getVerificationCodeExpireAt().before(new Timestamp(System.currentTimeMillis()))) {

            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpireAt(
                    Timestamp.valueOf(LocalDateTime.now().plusMinutes(5))
            );
            userRepository.save(user);
            sendVerificationEmail(user);
        }
    }

    @Override
    public LoginResponse verifyAndLogin(VerifyUserDto input, HttpServletResponse response)
            throws InvalidCredentialsException, VerificationCodeExpiredException {

        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        // Compare hashed verification code
        if (user.getVerificationCode() == null ||
                !passwordEncoder.matches(input.getVerificationCode(), user.getVerificationCode())) {
            throw new InvalidCredentialsException("Invalid verification code");
        }

        if (user.getVerificationCodeExpireAt().before(new Timestamp(System.currentTimeMillis()))) {
            throw new VerificationCodeExpiredException("Verification code has expired");
        }

        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpireAt(null);
        userRepository.save(user);

        return issueTokensAndBuildResponse(user, response);
    }

    @Override
    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        if (user.isEnabled() && user.isVerified()) {
            throw new IllegalStateException("Account is already verified");
        }

        sendNewVerificationCode(user);
    }

    @Override
    public void sendVerificationEmail(User user){
        String subject = "Account Verification";
        String verificationCode = user.getVerificationCode();
        String htmlMessage = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<div style='background-color: #007bff; color: white; padding: 20px; text-align: center;'>Welcome to SMS!</div>" +
                "<div style='padding: 20px; background-color: #f8f9fa;'>Please enter the verification code below to continue:</div>" +
                "<div style='padding: 20px; text-align: center;'>" +
                "<div style='font-size: 18px; margin-bottom: 10px;'>Verification Code:</div>" +
                "<div style='font-size: 32px; font-weight: bold; color: #007bff; letter-spacing: 5px;'>" +
                verificationCode +
                "</div>" +
                "</div>" +
                "</div>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send verification email: {}", e.getMessage());
            throw new RuntimeException("Failed to send verification email");
        }
    }
    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // FIX: Read refresh token from HTTP-only cookie, not Authorization header
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            LOGGER.warn("Refresh token cookie missing");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh token missing");
            return;

        }

        try {
            String userEmail = jwtService.extractUsername(refreshToken);

            if (userEmail == null) {
                return;
            }

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Token storedToken = tokenRepository.findByToken(refreshToken)
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

            if (storedToken.isRevoked() || storedToken.isExpired()) {
                throw new InvalidCredentialsException("Refresh token is revoked or expired");
            }

            if (jwtService.isTokenValid(refreshToken, user)) {
                // FIX: Revoke old tokens, generate NEW refresh token, save it — don't re-save the old one
                revokeAllUserTokens(user);
                String newAccessToken = jwtService.generateToken(user);
                String newRefreshToken = jwtService.generateRefreshToken(user);
                saveUserToken(user, newRefreshToken);

                // Set new refresh token cookie
                response.addCookie(createRefreshTokenCookie(newRefreshToken));
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);

                var authResponse = AuthResponse.builder()
                        .accessToken(newAccessToken)
                        .build();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(response.getOutputStream(), authResponse);
                response.getOutputStream().flush();

                //new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write refresh token response", e);
        } catch (InvalidCredentialsException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean logout(HttpServletRequest request,
                          HttpServletResponse response,
                          Authentication authentication) {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        final String jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(jwt).orElse(null);

        if (storedToken != null) {
            storedToken.setExpiresAt(Timestamp.valueOf(LocalDateTime.now()));
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }

        clearRefreshTokenCookie(response);
        return true;
    }


    @Transactional
    public String forgotPassword(String email) {

        String sql = """
            SELECT user_id
            FROM users
            WHERE email = ?
              AND deleted_at IS NULL
            """;

        List<Long> users = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getLong("user_id"),
                email
        );

        // Don't reveal whether the email exists
        if (users.isEmpty()) {
            return null;
        }

        Long userId = users.get(0);

        // Generate secure random token
        String rawToken = generateResetToken();

        // Hash token before storing
        String tokenHash = hashToken(rawToken);

        // Token expires in 15 minutes
        Timestamp expiresAt = Timestamp.from(
                Instant.now().plus(15, ChronoUnit.MINUTES)
        );

        // Optional: invalidate previous tokens
        String invalidateSql = """
            UPDATE password_reset_tokens
            SET used_at = CURRENT_TIMESTAMP
            WHERE user_id = ?
              AND used_at IS NULL
            """;

        jdbcTemplate.update(invalidateSql, userId);

        // Save new token
        String insertSql = """
            INSERT INTO password_reset_tokens
                (user_id, token_hash, expires_at, created_at)
            VALUES (?, ?, ?, CURRENT_TIMESTAMP)
            """;

        jdbcTemplate.update(
                insertSql,
                userId,
                tokenHash,
                expiresAt
        );

        String resetLink =
                "http://localhost:5173/reset-password?token=" + rawToken;

        emailService.sendPasswordResetEmail(
                email,
                resetLink
        );

        LOGGER.info(
                "Password reset email sent for user {}",
                userId
        );

        return rawToken;
    }

    private record PasswordResetTokenRow(
            Long id,
            Long userId
    ) {
    }

    @Transactional
    public void resetPassword(
            String rawToken,
            String newPassword
    ) {

        String tokenHash = hashToken(rawToken);

        String sql = """
            SELECT id, user_id
            FROM password_reset_tokens
            WHERE token_hash = ?
              AND used_at IS NULL
              AND expires_at > CURRENT_TIMESTAMP
            """;

        List<PasswordResetTokenRow> tokens = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new PasswordResetTokenRow(
                        rs.getLong("id"),
                        rs.getLong("user_id")
                ),
                tokenHash
        );

        if (tokens.isEmpty()) {
            throw new IllegalArgumentException(
                    "Invalid or expired password reset token"
            );
        }

        PasswordResetTokenRow token = tokens.get(0);

        // Update password
        String updatePasswordSql = """
            UPDATE users
            SET password = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE user_id = ?
            """;

        jdbcTemplate.update(
                updatePasswordSql,
                passwordEncoder.encode(newPassword),
                token.userId()
        );

        // Invalidate token
        String invalidateSql = """
            UPDATE password_reset_tokens
            SET used_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """;

        jdbcTemplate.update(
                invalidateSql,
                token.id()
        );

        LOGGER.info(
                "Password successfully reset for user {}",
                token.userId()
        );
    }
    /*
    /Helper Methods
     */
    /**
     * Generates and persists a new hashed verification code, then emails it to the user.
     */
    private String generateResetToken() {

        byte[] bytes = new byte[32];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);

        return HexFormat.of().formatHex(bytes);
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 algorithm not available",
                    e
            );
        }
    }

    private void sendNewVerificationCode(User user) {
        String plainCode = generateVerificationCode();

        // FIX: Store hashed code — never persist plaintext verification codes
        user.setVerificationCode(passwordEncoder.encode(plainCode));
        user.setVerificationCodeExpireAt(Timestamp.valueOf(LocalDateTime.now().plusMinutes(5)));
        userRepository.save(user);

        sendVerificationEmail(user, plainCode);
    }

    /**
     * Revokes old tokens, generates new JWT + refresh token, sets cookie, and returns LoginResponse.
     * FIX: Extracted shared login flow used by both authenticate() and verifyAndLogin()
     *      to avoid duplication and ensure consistent behaviour.
     */
    private LoginResponse issueTokensAndBuildResponse(
            User user,
            HttpServletResponse response
    ) {

        revokeAllUserTokens(user);

        Long userId = user.getUserId();

        List<String> roles = userRepository.findRolesByUserId(userId);

        String studentId = null;
        String teacherId = null;
        String parentId = null;

        for (String role : roles) {

            switch (role) {

                case "STUDENT" -> {
                    studentId = studentRepository
                            .findByUserId(userId.intValue())
                            .map(Student::getStudentId)
                            .orElse(null);
                }

                case "TEACHER" -> {
                    teacherId = teacherRepository
                            .findByUserId(userId.intValue())
                            .map(Teacher::getTeacherId)
                            .orElse(null);
                }

                case "PARENT" -> {
                    parentId = parentRepository
                            .findByUserId(userId.intValue())
                            .map(Parent::getParentId)
                            .orElse(null);
                }

                case "ADMIN" -> {
                    // Admin doesn't have a role-specific ID
                }
            }
        }

        String jwtToken = jwtService.generateToken(user);

        String refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(user, refreshToken);

        response.addCookie(
                createRefreshTokenCookie(refreshToken)
        );

        return LoginResponse.success(
                jwtToken,
                user,
                studentId,
                teacherId,
                parentId
        );
    }

    /**
     * FIX: Use SecureRandom — java.util.Random is not cryptographically secure.
     */
    private String generateVerificationCode() {
        int code = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private void sendVerificationEmail(User user, String plainCode) {
        String subject = "Account Verification";
        String htmlMessage = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<div style='background-color: #007bff; color: white; padding: 20px; text-align: center;'>Welcome to SMS!</div>" +
                "<div style='padding: 20px; background-color: #f8f9fa;'>Please enter the verification code below to continue:</div>" +
                "<div style='padding: 20px; text-align: center;'>" +
                "<div style='font-size: 18px; margin-bottom: 10px;'>Verification Code:</div>" +
                "<div style='font-size: 32px; font-weight: bold; color: #007bff; letter-spacing: 5px;'>" +
                plainCode +
                "</div></div></div>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send verification email to {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        // FIX: Use shared constant — path must match between set and clear
        cookie.setPath(REFRESH_TOKEN_COOKIE_PATH);
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        return cookie;
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        // FIX: Must match the path used when the cookie was set
        cookie.setPath(REFRESH_TOKEN_COOKIE_PATH);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * FIX: Read refresh token from cookie instead of Authorization header.
     */
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> REFRESH_TOKEN_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void saveUserToken(User user, String token) {
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + refreshTokenExpiredTime);
        Token tokenEntity = Token.builder()
                .user(user)
                .token(token)
                .tokenType(TokenType.BEARER)
                .expiresAt(expiresAt)
                .isExpired(false)
                .isRevoked(false)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();
        tokenRepository.save(tokenEntity);
    }

    private void revokeAllUserTokens(User user) {

        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());
        if (validUserTokens.isEmpty()) return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}


