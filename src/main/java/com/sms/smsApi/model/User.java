package com.sms.smsApi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username", unique = true)
    @JsonProperty("username")
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_enabled")
    private boolean enabled;

    @Column(name = "is_verified")
    private boolean verified;

    @Column(name = "is_locked")
    private boolean locked;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_expire_time")
    private Timestamp verificationCodeExpireAt;

    @Column(name = "attempted_count")
    private Integer attemptedCount;

    @Column(name = "lock_until")
    private Timestamp lockUntil;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private Timestamp createdAt  = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt  = new Timestamp(System.currentTimeMillis());

    @Column(name = "deleted_at")
    private Timestamp deletedAt;


    public User(Long userId, String firstName, String lastName, String username, String email, String password, boolean enabled, boolean verified, boolean locked, int attemptedCount, Timestamp lockUntil, Long createdBy, Timestamp createdAt, Timestamp deletedAt, Timestamp updatedAt) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.verified = verified;
        this.locked = locked;
        this.attemptedCount = attemptedCount;
        this.lockUntil = lockUntil;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.updatedAt = updatedAt;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /* ================= SECURITY ================= */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .toList();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getActualUsername(){
        return username;
    }
}