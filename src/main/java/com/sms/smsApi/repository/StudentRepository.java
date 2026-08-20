package com.sms.smsApi.repository;

import com.sms.smsApi.dto.requestDto.GenderCount;
import com.sms.smsApi.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    @Query(value = """
        SELECT s.student_id
        FROM students s
        ORDER BY s.student_id DESC
        FETCH FIRST 1 ROW ONLY
        """, nativeQuery = true)
    String findLatestStudentId();

    @Query("""
    SELECT s
    FROM Student s
    WHERE s.studentId = :studentId
    """)
    Student findStudentById(@Param("studentId") String studentId);

    @Query(value = """
    SELECT COUNT(*)
    FROM students
    WHERE student_id = :studentId
    """, nativeQuery = true)
    long countByStudentId(@Param("studentId") String studentId);

    @Query("""
        SELECT COUNT(s)
        FROM Student s
        WHERE UPPER(s.status) = 'ACTIVE'
    """)
    long countActiveStudents();

    @Query("""
        SELECT COUNT(s)
        FROM Student s
        WHERE UPPER(s.status)  = 'INACTIVE'
    """)
    long countInactiveStudents();


    @Query("""
    select s.gender, count(s) as total
    from Student s
    group by s.gender
    """)
    List<GenderCount> countStudentGender();

    Optional<Student> findByUserId(Integer userId);
}