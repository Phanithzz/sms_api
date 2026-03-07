package com.sms.smsApi.config;

import com.sms.smsApi.model.Role;
import com.sms.smsApi.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleConfig.class);

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            initializeRole(roleRepository, "ADMIN", "Administrator role with full access");
            initializeRole(roleRepository, "TEACHER", "Teacher role for instructors");
            initializeRole(roleRepository, "STUDENT", "Student role for learners");
            initializeRole(roleRepository, "PARENT", "Parent role for tracking their children");

            LOGGER.info("Role initialization completed");
        };
    }

    private void initializeRole(RoleRepository roleRepository, String roleName, String description) {
        if (!roleRepository.existsByRoleName(roleName)) {
            Role role = new Role(roleName, description);
            roleRepository.save(role);
            LOGGER.info("Created role: {}", roleName);
        } else {
            LOGGER.debug("Role already exists: {}", roleName);
        }
    }
}