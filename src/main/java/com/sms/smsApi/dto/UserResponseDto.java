package com.sms.smsApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sms.smsApi.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseDto {
    private Long userId;
    //@JsonProperty("username")
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    boolean verified;
    boolean enabled;
    private boolean locked;
    private Integer attemptedCount;
    private Timestamp lockUntil;
    private Long createdBy;
    private Timestamp createdAt  = new Timestamp(System.currentTimeMillis());
    private Timestamp updatedAt  = new Timestamp(System.currentTimeMillis());
    private Timestamp deletedAt ;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

}
