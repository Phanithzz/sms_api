package com.sms.smsApi.repository;

import com.sms.smsApi.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentRepository extends JpaRepository<Parent, String> {
    @Query(value = """
    SELECT p.parent_id
    FROM parents p
    WHERE p.parent_id LIKE :prefix || '%'
    ORDER BY p.parent_id DESC
    LIMIT 1
    """, nativeQuery = true)
    String findLatestParentId(@Param("prefix") String prefix);
}

