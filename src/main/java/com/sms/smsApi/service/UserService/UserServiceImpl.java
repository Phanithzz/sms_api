package com.sms.smsApi.service.UserService;

import com.sms.smsApi.dto.RegistrationDto;
import com.sms.smsApi.model.Role;
import com.sms.smsApi.model.User;
import com.sms.smsApi.model.UserRole;
import com.sms.smsApi.repository.RoleRepository;
import com.sms.smsApi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> allUsers() {
        return userRepository.findAll();

    }
    @Transactional
    public User createUser(RegistrationDto input, User currentUser) {
        // Check for duplicates
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new DuplicateKeyException("Email already exists");
        }

        if (userRepository.existsByUsername(input.getUsername())) {
            throw new DuplicateKeyException("Username already exists");
        }


        // Create user
        User user = new User();
        user.setUsername(input.getUsername());
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setEnabled(true);
        user.setVerified(false);
        user.setLocked(false);
//        user.setCreatedBy(currentUser.getUserId());
        user.setCreatedBy(null);
        user.setAttemptedCount(0);
        user.setLockUntil(null);
        user.setCreatedAt(user.getCreatedAt());
        user.setUpdatedAt(user.getUpdatedAt());
        user.setDeletedAt(user.getDeletedAt());
        userRepository.save(user);

        try {
            boolean isSuccess = insertRole(input, user.getUserId());
            if(!isSuccess){
                return null;
            }
            return userRepository.save(user);
        } catch (Exception e) {
            LOGGER.error("Failed to create user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user", e);
        }
    }
    @Override
    public User findUser(String usernameOrEmail) {

        String sql = """
        SELECT
            u.user_id,
            u.username,
            u.email,
            u.is_enabled,
            r.role_name
        FROM users u
        LEFT JOIN user_roles ur ON ur.user_id = u.user_id
        LEFT JOIN roles r ON r.role_id = ur.role_id
        WHERE u.username = ? OR u.email = ?
        """;

        return jdbcTemplate.query(sql, new Object[]{usernameOrEmail, usernameOrEmail}, rs -> {

            User user = null;

            while (rs.next()) {
                if (user == null) {
                    user = new User();
                    user.setUserId(rs.getLong("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setEnabled(rs.getBoolean("is_enabled"));
                }

                String roleName = rs.getString("role_name");
                if (roleName != null) {
                    Role role = new Role();
                    role.setRoleName(roleName);
                    user.getRoles().add(role);
                }

            }

            return user;
        });
    }


    private boolean insertRole(RegistrationDto input, Long userId) {
        String query = """
            INSERT INTO user_roles (user_id, role_id, created_at, updated_at)
            VALUES (?, ?, ?, ?)
            """;

        boolean allSuccess = true;
        LocalDateTime now = LocalDateTime.now();

        for (Integer roleId : input.getRoleId()) {
            Object[] params = {
                    userId,  // user_id
                    roleId,             // role_id
                    now,                // created_at
                    now                 // updated_at
            };

            int rowsAffected = jdbcTemplate.update(query, params);
            if (rowsAffected <= 0) {
                allSuccess = false;
            }
        }

        return allSuccess;
    }

}