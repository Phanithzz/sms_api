package com.sms.smsApi.repository;

import com.sms.smsApi.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    @Query(value = """
        SELECT s.student_id
        FROM students s
        ORDER BY s.student_id DESC
        FETCH FIRST 1 ROW ONLY
        """, nativeQuery = true)
    String findLatestStudentId();
}