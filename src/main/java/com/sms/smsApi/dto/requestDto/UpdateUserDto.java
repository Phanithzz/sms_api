package com.sms.smsApi.dto.requestDto;

import java.util.List;

public class UpdateUserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private Boolean enabled;
    private Boolean locked;
    private List<Integer> roleIds;

    // Getters & Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Boolean getLocked() { return locked; }
    public void setLocked(Boolean locked) { this.locked = locked; }

    public List<Integer> getRoleIds() { return roleIds; }
    public void setRoleIds(List<Integer> roleIds) { this.roleIds = roleIds; }
}
