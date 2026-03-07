package com.sms.smsApi.repository;

import com.sms.smsApi.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by role name
     * @param roleName the role name
     * @return Optional containing the role if found
     */
    Optional<Role> findByRoleName(String roleName);

    /**
     * Find role by role name (case-insensitive)
     * @param roleName the role name
     * @return Optional containing the role if found
     */
    Optional<Role> findByRoleNameIgnoreCase(String roleName);

    /**
     * Check if role exists by name
     * @param roleName the role name
     * @return true if exists, false otherwise
     */
    boolean existsByRoleName(String roleName);

    /**
     * Check if role exists by name (case-insensitive)
     * @param roleName the role name
     * @return true if exists, false otherwise
     */
    boolean existsByRoleNameIgnoreCase(String roleName);

    /**
     * Find all roles ordered by role name
     * @return list of roles
     */
    List<Role> findAllByOrderByRoleNameAsc();

    /**
     * Find roles by name containing search term
     * @param searchTerm the search term
     * @return list of matching roles
     */
    @Query("SELECT r FROM Role r WHERE LOWER(r.roleName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Role> searchByRoleName(@Param("searchTerm") String searchTerm);

    /**
     * Count total number of roles
     * @return count of roles
     */
    @Query("SELECT COUNT(r) FROM Role r")
    long countAllRoles();

    /**
     * Delete role by role name
     * @param roleName the role name
     */
    void deleteByRoleName(String roleName);
}