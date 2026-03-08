package com.sms.smsApi.controller;

import com.sms.smsApi.dto.RegistrationDto;
import com.sms.smsApi.dto.UserReponseDto;
import com.sms.smsApi.model.User;
import com.sms.smsApi.reponse.ApiResponse;
import com.sms.smsApi.repository.UserRepository;
import com.sms.smsApi.service.UserService.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

//    @GetMapping("/me")
//    public ResponseEntity<User> authenticatedUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//
//        //User user = userRepository.findByUsername(userDetails.getUsername());
//
//        return ResponseEntity.ok(user);
//    }


    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers(
            @AuthenticationPrincipal User currentUser  // ← Spring injects this automatically
    ) {
        LOGGER.info("Current user authorities: {}", currentUser.getAuthorities());
        List<User> users = userService.allUsers();



        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> createUser(
            @RequestBody RegistrationDto input,
            @AuthenticationPrincipal User currentUser) {  // Inject directly

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
}
