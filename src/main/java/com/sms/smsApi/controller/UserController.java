package com.sms.smsApi.controller;

import com.sms.smsApi.dto.RegistrationDto;
import com.sms.smsApi.dto.UserResponseDto;
import com.sms.smsApi.dto.requestDto.UpdateUserDto;
import com.sms.smsApi.model.User;
import com.sms.smsApi.reponse.ApiResponse;
import com.sms.smsApi.repository.UserRepository;
import com.sms.smsApi.service.UserService.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "username", "firstName", "lastName");

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        LOGGER.debug("REST request to get all users - page: {}, size: {}, sort: {}", page, size, sort);
        Pageable pageable = createPageable(page, size, sort);
        Page<UserResponseDto> response = userService.allUsers(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", response));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> createUser(
            @RequestBody RegistrationDto input,
            @AuthenticationPrincipal User currentUser) {
        try {
            User createdUser = userService.createUser(input, currentUser);
            if (createdUser != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(
                        new ApiResponse<>(true, "User created successfully", createdUser)
                );
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Server Error", null)
            );
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse<>(false, "Conflict User ID!", null)
            );
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserResponseDto>>> searchUsers(
            @RequestParam @NotBlank String searchTerm,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        LOGGER.debug("REST request to search users with term: '{}' - page: {}, size: {}, sort: {}",
                searchTerm, page, size, sort);
        Pageable pageable = createPageable(page, size, sort);
        Page<UserResponseDto> response = userService.searchUsers(searchTerm, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "User search successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserDto dto,
            @AuthenticationPrincipal User currentUser) {
        UserResponseDto updated = userService.updateUser(id, dto, currentUser);
        return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        userService.deleteUser(id, currentUser);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(
            @PathVariable Long id) {

        LOGGER.debug("REST request to get user by id: {}", id);

        UserResponseDto user = userService.getUserById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "User retrieved successfully", user)
        );
    }
    /**
     * Creates a Pageable with validated sorting.
     * Defaults to sorting by id ASC if the field is invalid or blank.
     */
    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");

        String property = sortParams[0].isBlank() ? "id" : sortParams[0];
        if (!ALLOWED_SORT_FIELDS.contains(property)) {
            LOGGER.warn("Invalid sort field '{}', falling back to 'id'", property);
            property = "id";
        }

        Sort.Direction direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}