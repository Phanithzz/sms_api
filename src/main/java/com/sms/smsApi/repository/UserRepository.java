package com.sms.smsApi.repository;

import com.sms.smsApi.dto.UserResponseDto;
import com.sms.smsApi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByUserId(Long userId);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND u.enabled = true")
    Page<User> findAllActive(Pageable pageable);
    /**
     * Searches users by username, first name, or last name (case-insensitive).
     *
     * @param searchTerm the term to search for
     * @param pageable   pagination information
     * @return Page of matching users
     */
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<User> searchActiveUsers(@Param("searchTerm") String searchTerm, Pageable pageable);
}