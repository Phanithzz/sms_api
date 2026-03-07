package com.sms.smsApi.repository;

import java.util.List;
import java.util.Optional;

import com.sms.smsApi.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("""
        SELECT t FROM Token t
        WHERE t.user.id = :userId
          AND t.isExpired = false
          AND t.isRevoked = false
    """)
    List<Token> findAllValidTokenByUser(@Param("userId") Long userId);

    Optional<Token> findByToken(String token);

    void deleteAllByIsExpiredTrueOrIsRevokedTrue();

}
