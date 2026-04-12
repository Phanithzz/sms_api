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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")

public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    private final UserRepository userRepository;
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public ResponseEntity<Object> getAllUsers(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        LOGGER.info("Current user authorities: {}", currentUser.getAuthorities());
        Collection<? extends GrantedAuthority> userRoles = currentUser.getAuthorities();

        boolean hasAdmin = userRoles.stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if(!hasAdmin){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(
                            false, "Unauthorized user", null
                    )
            );
        }
        //List<UserResponseDto> users = userService.allUsers();
        LOGGER.debug("REST request to get all users - page: {}, size: {}, sort: {}", page, size, sort);

        Pageable pageable = createPageable(page, size, sort);
        Page<UserResponseDto> response = userService.allUsers(pageable);
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Users retrieved successfully", response
        ));
    }

    @PostMapping("/create")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> createUser(
            @RequestBody RegistrationDto input,
            @AuthenticationPrincipal User currentUser) {
        Collection<? extends GrantedAuthority> userRoles = currentUser.getAuthorities();

        boolean hasAdmin = userRoles.stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if(!hasAdmin){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(
                            false, "Unauthorized user", null
                    )
            );
        }

        try{
            User createdUser = userService.createUser(input, currentUser);

            if(createdUser != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(
                        new ApiResponse<>(true, "User created successfully", createdUser)
                );
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Server Error", null)
            );
        } catch (DuplicateKeyException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponse<>(false, "Conflict User ID!", null)
            );
        }
    }

    /**
     * Searches users by term (username, first name, or last name).
     *
     * @param searchTerm the search term
     * @param page       page number (0-based, default: 0)
     * @param size       page size (default: 20)
     * @param sort       sort criteria (default: id,asc)
     * @return page of matching user responses with HTTP 200
     */
    @GetMapping("/search")
    public ResponseEntity<Object> searchUsers(
            @RequestParam @NotBlank String searchTerm,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        LOGGER.debug("REST request to search users with term: '{}' - page: {}, size: {}, sort: {}",
                searchTerm, page, size, sort);

        Pageable pageable = createPageable(page, size, sort);
        Page<UserResponseDto> response = userService.searchUsers(searchTerm, pageable);
        return ResponseEntity.ok(
                new ApiResponse<>(
                    true,
                    "User search successfully",
                    response
                )
        );
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserDto dto,
            @AuthenticationPrincipal User currentUser) {

            return ResponseEntity.ok(userService.updateUser(id, dto, currentUser));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        userService.deleteUser(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    /**
     * Helper method to create Pageable with sorting.
     *
     * @param page page number
     * @param size page size
     * @param sort sort criteria
     * @return Pageable instance
     */
    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String property = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}
