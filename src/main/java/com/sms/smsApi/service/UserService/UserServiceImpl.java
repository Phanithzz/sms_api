package com.sms.smsApi.service.UserService;

import com.sms.smsApi.dto.RegistrationDto;
import com.sms.smsApi.dto.UserResponseDto;
import com.sms.smsApi.dto.requestDto.UpdateUserDto;
import com.sms.smsApi.mapper.UserMapper;
import com.sms.smsApi.model.Parent;
import com.sms.smsApi.model.Role;
import com.sms.smsApi.model.User;
import com.sms.smsApi.model.UserRole;
import com.sms.smsApi.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;


@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate, UserMapper userMapper, StudentRepository studentRepository, TeacherRepository teacherRepository, ParentRepository parentRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.parentRepository = parentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> allUsers(Pageable pageable) {
        LOGGER.debug("Retrieving all users with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<User> users = userRepository.findAllActive(pageable);
        return users.map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> searchUsers(String searchTerm, Pageable pageable) {
        LOGGER.debug("Searching users with term: '{}', page={}, size={}",
                searchTerm, pageable.getPageNumber(), pageable.getPageSize());

        Page<User> users = userRepository.searchActiveUsers(searchTerm, pageable);
        return users.map(userMapper::toResponse);
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
        user.setCreatedBy(currentUser.getUserId());
        // user.setCreatedBy(null);
        user.setAttemptedCount(0);
        user.setLockUntil(null);
        user.setCreatedAt(user.getCreatedAt());
        user.setUpdatedAt(user.getUpdatedAt());
        user.setDeletedAt(user.getDeletedAt());
        User savedUser = userRepository.save(user);

        // Long newUserId = savedUser.getUserId();

        try {
            boolean isSuccess = insertRole(input, user.getUserId());
            if (!isSuccess) {
                return null;
            }

            for (Integer roleId : input.getRoleId()) {
                if (roleId.equals(UserRole.STUDENT.getCode())) {
                    boolean insertStudent = insertStudentViaUser(input, savedUser.getUserId(), currentUser.getUserId());
                    if (!insertStudent) {
                        LOGGER.error("Error insert student for student ID: {}", savedUser.getUserId());
                    }
                } else if (roleId.equals(UserRole.TEACHER.getCode())) {
                    boolean insertTeacher = insertTeacherViaUser(input, savedUser.getUserId(), currentUser.getUserId());
                    if (!insertTeacher) {
                        LOGGER.error("Error insert teacher for teacher ID: {}", savedUser.getUserId());
                    }
                }
                else if (roleId.equals(UserRole.PARENT.getCode())) {
                    boolean insertParent = insertParentViaUser(input,savedUser.getUserId(), user.getUserId());
                    if(!insertParent){
                        LOGGER.error("Error insert parent for parent ID: {}", savedUser.getUserId());
                    }
                }
                else {
                    LOGGER.info("Admin role has created for user {}", savedUser.getUserId());
                }
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

    @Override
    @Transactional
    public UserResponseDto updateUser(Long userId, UpdateUserDto input, User currentUser) {
        LOGGER.debug("Updating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));

        // Prevent updating a soft-deleted user
        if (user.getDeletedAt() != null) {
            throw new IllegalStateException("Cannot update a deleted user");
        }

        // Check email uniqueness (only if changed)
        if (input.getEmail() != null && !input.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(input.getEmail())) {
                throw new DuplicateKeyException("Email already exists: " + input.getEmail());
            }
            user.setEmail(input.getEmail());
        }

        // Check username uniqueness (only if changed)
        if (input.getUsername() != null && !input.getUsername().equals(user.getActualUsername())) {
            if (userRepository.existsByUsername(input.getUsername())) {
                throw new DuplicateKeyException("Username already exists: " + input.getUsername());
            }
            user.setUsername(input.getUsername());
        }

        // Apply field updates (only non-null fields)
        if (input.getFirstName() != null) user.setFirstName(input.getFirstName());
        if (input.getLastName() != null) user.setLastName(input.getLastName());
        if (input.getEnabled() != null) user.setEnabled(input.getEnabled());
        if (input.getLocked() != null) user.setLocked(input.getLocked());

        user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        // Update roles if provided
        if (input.getRoleIds() != null && !input.getRoleIds().isEmpty()) {
            deleteExistingRoles(userId);
            insertUpdatedRoles(input.getRoleIds(), userId);
        }

        User saved = userRepository.save(user);
        LOGGER.info("User updated successfully: userId={}, updatedBy={}", userId, currentUser.getUserId());
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId, User currentUser) {
        LOGGER.debug("Soft-deleting user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));

        // Guard: already deleted
        if (user.getDeletedAt() != null) {
            throw new IllegalStateException("User is already deleted");
        }

        // Guard: prevent self-deletion
        if (userId.equals(currentUser.getUserId())) {
            throw new IllegalArgumentException("You cannot delete your own account");
        }

        // Soft delete — set deletedAt, disable account
        user.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
        user.setEnabled(false);

        userRepository.save(user);
        LOGGER.info("User soft-deleted: userId={}, deletedBy={}", userId, currentUser.getUserId());
    }


    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponse(user);
    }
// ── Private helpers ──────────────────────────────────────────────────────────

    private void deleteExistingRoles(Long userId) {
        String sql = "DELETE FROM user_roles WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    private void insertUpdatedRoles(List<Integer> roleIds, Long userId) {
        String sql = """
                INSERT INTO user_roles (user_id, role_id, created_at, updated_at)
                VALUES (?, ?, ?, ?)
                """;
        LocalDateTime now = LocalDateTime.now();
        for (Integer roleId : roleIds) {
            int rows = jdbcTemplate.update(sql, userId, roleId, now, now);
            if (rows <= 0) {
                throw new RuntimeException("Failed to insert role " + roleId + " for user " + userId);
            }
        }
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

    private String generateStudentID() {

        String latestId = studentRepository.findLatestStudentId();

        if (latestId == null) {
            return "ST000001";
        }

        int number = Integer.parseInt(latestId.substring(2));
        number++;

        return String.format("ST%06d", number);
    }

    private String generateTeacherID() {
        String latest = teacherRepository.findLatestTeacherIdByPrefix("TH");

        if (latest == null) {
            return "TH000001";
        }

        int seq = Integer.parseInt(latest.substring(2)); // remove "TH"
        return String.format("TH%06d", seq + 1);
    }

    private String generateParentID() {
        String latest = parentRepository.findLatestParentId("P");

        if (latest == null) {
            return "P000001";
        }

        int seq = Integer.parseInt(latest.substring(1)); // remove "P"
        return String.format("P%06d", seq + 1);
    }


    private boolean insertStudentViaUser(RegistrationDto input, Long newUserId, Long createdUserId) {
        String sql = """
                INSERT INTO students (student_id, user_id, first_name_en, last_name_en, email, enrolled_date, created_at, created_by)
                VALUES (?,?,?,?,?,?,?,?)
                """;
        String studentId = generateStudentID();

        Object[] params = {studentId, newUserId, input.getFirstName(), input.getLastName(),
                input.getEmail(), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), createdUserId
        };

        int success = jdbcTemplate.update(sql, params);

        return success > 0;
    }

    private boolean insertTeacherViaUser(RegistrationDto input, Long newUserId, Long createdUserId) {
        String sql = """
                INSERT INTO teachers (teacher_id, user_id, first_name_en, last_name_en, email, hired_date, created_at, created_by)
                VALUES (?,?,?,?,?,?,?,?)
                """;
        String id = generateTeacherID();

        Object[] params = {id, newUserId, input.getFirstName(), input.getLastName(),
                input.getEmail(), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), createdUserId
        };

        int success = jdbcTemplate.update(sql, params);

        return success > 0;
    }


    // Note: insert only father's name first
    private boolean insertParentViaUser(RegistrationDto input, Long newUserId, Long createdUserId) {
        String sql = """
                INSERT INTO parents (parent_id, user_id, father_name_en ,created_at)
                VALUES (?,?,?,?)
                """;
        String id = generateParentID();

        Object[] params = {id, newUserId, input.getFirstName() + input.getLastName(),
                 new Timestamp(System.currentTimeMillis())
        };

        int success = jdbcTemplate.update(sql, params);


        return success > 0;
    }

    private UserResponseDto mapToResponse(User user) {
        UserResponseDto dto = new UserResponseDto();

        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());

        dto.setRoles(user.getRoles());
        dto.setEnabled(user.isEnabled());

        return dto;
    }

}