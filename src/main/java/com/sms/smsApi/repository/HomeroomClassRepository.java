package com.sms.smsApi.repository;

import com.sms.smsApi.model.HomeroomClass;
import com.sms.smsApi.rowMapper.HomeroomRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HomeroomClassRepository {

    private final JdbcTemplate jdbcTemplate;

    private final HomeroomRowMapper rowMapper =
            new HomeroomRowMapper();


    // Find homeroom class by ID
    public HomeroomClass findById(Integer classId) {

        String sql = """
                SELECT
                    class_id,
                    class_code,
                    grade_level,
                    shift,
                    homeroom_teacher_id,
                    classroom_id,
                    academic_year_id,
                    max_capacity,
                    enrolled_count,
                    created_at,
                    updated_at
                FROM homeroom_classes
                WHERE class_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                rowMapper,
                classId
        ).stream().findFirst().orElse(null);
    }


    // Check if class exists
    public boolean exists(Integer classId) {

        String sql = """
                SELECT COUNT(*)
                FROM homeroom_classes
                WHERE class_id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                classId
        );

        return count != null && count > 0;
    }


    // Increase enrolled count
    public void incrementEnrollment(Integer classId) {

        String sql = """
                UPDATE homeroom_classes
                SET enrolled_count = enrolled_count + 1,
                    updated_at = CURRENT_TIMESTAMP
                WHERE class_id = ?
                """;

        jdbcTemplate.update(sql, classId);
    }


    // Decrease enrolled count
    public void decrementEnrollment(Integer classId) {

        String sql = """
                UPDATE homeroom_classes
                SET enrolled_count =
                    CASE
                        WHEN enrolled_count > 0
                        THEN enrolled_count - 1
                        ELSE 0
                    END,
                    updated_at = CURRENT_TIMESTAMP
                WHERE class_id = ?
                """;

        jdbcTemplate.update(sql, classId);
    }


    // Check whether class is full
    public boolean isFull(Integer classId) {

        String sql = """
                SELECT enrolled_count >= max_capacity
                FROM homeroom_classes
                WHERE class_id = ?
                """;

        Boolean full = jdbcTemplate.queryForObject(
                sql,
                Boolean.class,
                classId
        );

        return Boolean.TRUE.equals(full);
    }
}