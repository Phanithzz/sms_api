package com.sms.smsApi.service.UserService;

import com.sms.smsApi.dto.RegistrationDto;
import com.sms.smsApi.model.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    List<User> allUsers();
    User createUser(RegistrationDto input, User user);
    User findUser(String usernameOrEmail);
}
