package com.sms.smsApi.service.UserService;

import com.sms.smsApi.dto.RegistrationDto;
import com.sms.smsApi.dto.UserResponseDto;
import com.sms.smsApi.dto.requestDto.UpdateUserDto;
import com.sms.smsApi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {
    Page<UserResponseDto> allUsers(Pageable pageable);
    User createUser(RegistrationDto input, User user);
    User findUser(String usernameOrEmail);
    Page<UserResponseDto> searchUsers(String searchTerm, Pageable pageable);
    UserResponseDto updateUser(Long userId, UpdateUserDto input, User currentUser);
    void deleteUser(Long userId, User currentUser);
}
