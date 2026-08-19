package com.sms.smsApi.repository;

import com.sms.smsApi.dto.requestDto.ClassEnrollmentSummary;
import com.sms.smsApi.dto.requestDto.HomeroomClassOption;
import com.sms.smsApi.model.HomeroomClass;
import com.sms.smsApi.rowMapper.HomeroomRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HomeroomClassRepository {

    private final JdbcTemplate jdbcTemplate;

    private final HomeroomRowMapper rowMapper =
            new HomeroomRowMapper();

    public List<HomeroomClassOption> findEnrollmentOptions(
            Integer academicYearId) {

        String sql = """
            SELECT
                class_id,
                class_code,
                grade_level,
                shift,
                homeroom_teacher_id,
                classroom_id,
                max_capacity,
                enrolled_count,
                (max_capacity - enrolled_count)
                    AS available_capacity
            FROM homeroom_classes
            WHERE academic_year_id = ?
              AND enrolled_count < max_capacity
            ORDER BY grade_level, class_code
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    HomeroomClassOption option =
                            new HomeroomClassOption();

                    option.setClassId(
                            rs.getInt("class_id"));

                    option.setClassCode(
                            rs.getString("class_code"));

                    option.setGradeLevel(
                            rs.getInt("grade_level"));

                    option.setShift(
                            rs.getString("shift"));

                    option.setHomeroomTeacherId(
                            rs.getString("homeroom_teacher_id"));

                    option.setClassroomId(
                            rs.getInt("classroom_id"));

                    option.setMaxCapacity(
                            rs.getInt("max_capacity"));

                    option.setEnrolledCount(
                            rs.getInt("enrolled_count"));

                    option.setAvailableCapacity(
                            rs.getInt("available_capacity"));

                    return option;
                },
                academicYearId
        );
    }
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

    public List<ClassEnrollmentSummary> getDashboardSummary() {

        String sql = """
            SELECT
                hc.class_id,
                hc.class_code,
                hc.grade_level,
                hc.shift,
                hc.max_capacity,
                hc.enrolled_count,

                (hc.max_capacity - hc.enrolled_count)
                    AS available_capacity,

                COUNT(cs.section_id)
                    AS section_count

            FROM homeroom_classes hc

            LEFT JOIN class_sections cs
                ON cs.homeroom_class_id = hc.class_id

            GROUP BY
                hc.class_id,
                hc.class_code,
                hc.grade_level,
                hc.shift,
                hc.max_capacity,
                hc.enrolled_count

            ORDER BY
                hc.grade_level,
                hc.class_code
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        new ClassEnrollmentSummary(
                                rs.getInt("class_id"),
                                rs.getString("class_code"),
                                rs.getInt("grade_level"),
                                rs.getString("shift"),
                                rs.getInt("max_capacity"),
                                rs.getInt("enrolled_count"),
                                rs.getInt("available_capacity"),
                                rs.getInt("section_count")
                        )
        );
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