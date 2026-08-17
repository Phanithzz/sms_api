package com.sms.smsApi.repository;

import com.sms.smsApi.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String> {
    @Query(value = """
    SELECT t.teacher_id
    FROM teachers t
    WHERE t.teacher_id LIKE :prefix || '%'
    ORDER BY t.teacher_id DESC
    LIMIT 1
    """, nativeQuery = true)
    String findLatestTeacherIdByPrefix(@Param("prefix") String prefix);

    boolean existsByEmail(String email);

    @Query("""
    SELECT COUNT(t)
    FROM Teacher t
    WHERE t.deletedAt IS NULL
""")
    long countActiveTeachers();
}
