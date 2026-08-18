package com.sms.smsApi.repository;

import com.sms.smsApi.dto.EnrollmentResponse;
import com.sms.smsApi.dto.requestDto.EnrollmentRequest;
import com.sms.smsApi.model.enums.EnrollmentStatus;
import com.sms.smsApi.rowMapper.EnrollmentRowmapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepository {

    private final JdbcTemplate jdbc;
    private final RowMapper<EnrollmentResponse> enrollmentMapper =
            new EnrollmentRowmapper();

    private final KeyHolder keyHolder =
            new GeneratedKeyHolder();

    public boolean exists(String studentId,
                          Integer sectionId) {

        Integer count = jdbc.queryForObject(
                """
                SELECT COUNT(*)
                FROM enrollments
                WHERE student_id = ?
                AND section_id = ?
                AND status = 'Active'
                """,
                Integer.class,
                studentId,
                sectionId
        );

        return count != null && count > 0;
    }

    public Integer insert(EnrollmentRequest request) {

        String sql = """
            INSERT INTO enrollments (
                student_id,
                section_id,
                enrolled_date,
                status
            )
            VALUES (?, ?, CURRENT_DATE, 'Active')
            RETURNING enrollment_id
            """;

        return jdbc.queryForObject(
                sql,
                Integer.class,
                request.getStudentId(),
                request.getSectionId()
        );
    }

    public Optional<EnrollmentResponse> findById(Integer id) {

        String sql = """
        SELECT enrollment_id,
               student_id,
               section_id,
               status
        FROM enrollments
        WHERE enrollment_id = ?
        """;

        List<EnrollmentResponse> list =
                jdbc.query(sql, enrollmentMapper, id);

        return list.stream().findFirst();
    }


    public List<EnrollmentResponse> findActiveByStudent(String studentId) {

        String sql = """
        SELECT enrollment_id,
               student_id,
               section_id,
               status
        FROM enrollments
        WHERE student_id = ?
          AND status = 'ACTIVE'
        ORDER BY enrollment_id DESC
        """;

        return jdbc.query(sql, enrollmentMapper, studentId);
    }

    public void updateStatus(Integer id,
                             EnrollmentStatus status) {

        jdbc.update("""
        UPDATE enrollments
        SET status = ?
        WHERE enrollment_id = ?
        """,
                status.name(),
                id);
    }

    public void delete(Integer id) {

        jdbc.update("""
        DELETE FROM enrollments
        WHERE enrollment_id = ?
        """,
                id);
    }

    public void decrementSectionCount(Integer sectionId) {

        jdbc.update("""
        UPDATE class_sections
        SET enrolled_count = enrolled_count - 1
        WHERE section_id = ?
        """,
                sectionId);
    }

    public Page<EnrollmentResponse> search(
            String studentId,
            Integer sectionId,
            EnrollmentStatus status,
            Pageable pageable) {

        StringBuilder sql = new StringBuilder("""
        SELECT enrollment_id,
               student_id,
               section_id,
               status
        FROM enrollments
        WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (studentId != null && !studentId.isBlank()) {
            sql.append(" AND student_id = ?");
            params.add(studentId);
        }

        if (sectionId != null) {
            sql.append(" AND section_id = ?");
            params.add(sectionId);
        }

        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status.name());
        }

        sql.append(" ORDER BY enrollment_id DESC");
        sql.append(" LIMIT ? OFFSET ?");

        params.add(pageable.getPageSize());
        params.add(pageable.getOffset());

        List<EnrollmentResponse> content =
                jdbc.query(sql.toString(), enrollmentMapper, params.toArray());

        // Count query
        StringBuilder countSql = new StringBuilder("""
        SELECT COUNT(*)
        FROM enrollments
        WHERE 1=1
        """);

        List<Object> countParams = new ArrayList<>();

        if (studentId != null && !studentId.isBlank()) {
            countSql.append(" AND student_id = ?");
            countParams.add(studentId);
        }

        if (sectionId != null) {
            countSql.append(" AND section_id = ?");
            countParams.add(sectionId);
        }

        if (status != null) {
            countSql.append(" AND status = ?");
            countParams.add(status.name());
        }

        Long total = jdbc.queryForObject(
                countSql.toString(),
                Long.class,
                countParams.toArray());

        return new PageImpl<>(content, pageable, total);
    }
}
