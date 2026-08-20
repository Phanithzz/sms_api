package com.sms.smsApi.repository;

import com.sms.smsApi.dto.requestDto.ScheduleRequest;
import com.sms.smsApi.dto.requestDto.ScheduleResponse;
import com.sms.smsApi.rowMapper.ScheduleRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<ScheduleResponse> scheduleMapper =
            new ScheduleRowMapper();


    // =========================================================
    // INSERT
    // =========================================================

    public Integer insert(
            ScheduleRequest request) {

        String sql = """
                INSERT INTO schedules (
                    section_id,
                    homeroom_class_id,
                    classroom_id,
                    academic_year_id,
                    day_of_week,
                    period_number,
                    start_time,
                    end_time
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING schedule_id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Integer.class,

                request.getSectionId(),
                request.getHomeroomClassId(),
                request.getClassroomId(),
                request.getAcademicYearId(),
                request.getDayOfWeek(),
                request.getPeriodNumber(),
                request.getStartTime(),
                request.getEndTime()
        );
    }


    // =========================================================
    // FIND BY ID
    // =========================================================

    public Optional<ScheduleResponse> findById(
            Integer id) {

        String sql = """
                SELECT
                    s.schedule_id,

                    s.section_id,
                    cs.section_code,

                    cs.subject_id,
                    sub.subject_code,
                    sub.subject_name_en,
                    sub.subject_name_kh,

                    s.homeroom_class_id,
                    hc.class_code,
                    hc.grade_level,

                    cs.teacher_id,

                    CONCAT(
                        t.first_name_en,
                        ' ',
                        t.last_name_en
                    ) AS teacher_name,

                    s.classroom_id,
                    c.classroom_name,

                    s.academic_year_id,

                    s.day_of_week,
                    s.period_number,
                    s.start_time,
                    s.end_time

                FROM schedules s

                JOIN class_sections cs
                    ON cs.section_id = s.section_id

                JOIN subjects sub
                    ON sub.subject_id = cs.subject_id

                JOIN homeroom_classes hc
                    ON hc.class_id = s.homeroom_class_id

                JOIN teachers t
                    ON t.teacher_id = cs.teacher_id

                JOIN classrooms c
                    ON c.classroom_id = s.classroom_id

                WHERE s.schedule_id = ?
                """;

        List<ScheduleResponse> result =
                jdbcTemplate.query(
                        sql,
                        scheduleMapper,
                        id
                );

        return result.stream().findFirst();
    }


    // =========================================================
    // GET CLASS TIMETABLE
    // =========================================================

    public List<ScheduleResponse> findByClass(
            Integer homeroomClassId,
            Integer academicYearId) {

        String sql = """
                SELECT
                    s.schedule_id,

                    s.section_id,
                    cs.section_code,

                    cs.subject_id,
                    sub.subject_code,
                    sub.subject_name_en,
                    sub.subject_name_kh,

                    s.homeroom_class_id,
                    hc.class_code,
                    hc.grade_level,

                    cs.teacher_id,

                    CONCAT(
                        t.first_name_en,
                        ' ',
                        t.last_name_en
                    ) AS teacher_name,

                    s.classroom_id,
                    c.classroom_name,

                    s.academic_year_id,

                    s.day_of_week,
                    s.period_number,
                    s.start_time,
                    s.end_time

                FROM schedules s

                JOIN class_sections cs
                    ON cs.section_id = s.section_id

                JOIN subjects sub
                    ON sub.subject_id = cs.subject_id

                JOIN homeroom_classes hc
                    ON hc.class_id = s.homeroom_class_id

                JOIN teachers t
                    ON t.teacher_id = cs.teacher_id

                JOIN classrooms c
                    ON c.classroom_id = s.classroom_id

                WHERE s.homeroom_class_id = ?
                  AND s.academic_year_id = ?

                ORDER BY
                    CASE s.day_of_week
                        WHEN 'MONDAY' THEN 1
                        WHEN 'TUESDAY' THEN 2
                        WHEN 'WEDNESDAY' THEN 3
                        WHEN 'THURSDAY' THEN 4
                        WHEN 'FRIDAY' THEN 5
                        WHEN 'SATURDAY' THEN 6
                    END,
                    s.period_number
                """;

        return jdbcTemplate.query(
                sql,
                scheduleMapper,
                homeroomClassId,
                academicYearId
        );
    }


    // =========================================================
    // GET TEACHER TIMETABLE
    // =========================================================

    public List<ScheduleResponse> findByTeacher(
            String teacherId,
            Integer academicYearId) {

        String sql = """
                SELECT
                    s.schedule_id,

                    s.section_id,
                    cs.section_code,

                    cs.subject_id,
                    sub.subject_code,
                    sub.subject_name_en,
                    sub.subject_name_kh,

                    s.homeroom_class_id,
                    hc.class_code,
                    hc.grade_level,

                    cs.teacher_id,

                    CONCAT(
                        t.first_name_en,
                        ' ',
                        t.last_name_en
                    ) AS teacher_name,

                    s.classroom_id,
                    c.classroom_name,

                    s.academic_year_id,

                    s.day_of_week,
                    s.period_number,
                    s.start_time,
                    s.end_time

                FROM schedules s

                JOIN class_sections cs
                    ON cs.section_id = s.section_id

                JOIN subjects sub
                    ON sub.subject_id = cs.subject_id

                JOIN homeroom_classes hc
                    ON hc.class_id = s.homeroom_class_id

                JOIN teachers t
                    ON t.teacher_id = cs.teacher_id

                JOIN classrooms c
                    ON c.classroom_id = s.classroom_id

                WHERE cs.teacher_id = ?
                  AND s.academic_year_id = ?

                ORDER BY
                    CASE s.day_of_week
                        WHEN 'MONDAY' THEN 1
                        WHEN 'TUESDAY' THEN 2
                        WHEN 'WEDNESDAY' THEN 3
                        WHEN 'THURSDAY' THEN 4
                        WHEN 'FRIDAY' THEN 5
                        WHEN 'SATURDAY' THEN 6
                    END,
                    s.period_number
                """;

        return jdbcTemplate.query(
                sql,
                scheduleMapper,
                teacherId,
                academicYearId
        );
    }


    // =========================================================
    // CHECK CONFLICT
    // =========================================================

    public boolean existsConflict(
            Integer homeroomClassId,
            Integer classroomId,
            Integer sectionId,
            Integer academicYearId,
            String dayOfWeek,
            Integer periodNumber) {

        String sql = """
                SELECT COUNT(*)
                FROM schedules s

                JOIN class_sections cs
                    ON cs.section_id = s.section_id

                WHERE s.academic_year_id = ?

                  AND s.day_of_week = ?

                  AND s.period_number = ?

                  AND (
                        s.homeroom_class_id = ?
                        OR s.classroom_id = ?
                        OR cs.teacher_id = (
                            SELECT teacher_id
                            FROM class_sections
                            WHERE section_id = ?
                        )
                  )
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        academicYearId,
                        dayOfWeek,
                        periodNumber,
                        homeroomClassId,
                        classroomId,
                        sectionId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // DELETE
    // =========================================================

    public void delete(Integer id) {

        jdbcTemplate.update(
                """
                DELETE FROM schedules
                WHERE schedule_id = ?
                """,
                id
        );
    }


    public List<ScheduleResponse> findByStudent(
            String studentId,
            Integer academicYearId
    ) {
        String sql = """
            SELECT
                s.schedule_id,

                s.section_id,
                cs.section_code,

                cs.subject_id,
                sub.subject_code,
                sub.subject_name_en,
                sub.subject_name_kh,

                s.homeroom_class_id,
                hc.class_code,
                hc.grade_level,

                cs.teacher_id,

                CONCAT(
                    t.first_name_en,
                    ' ',
                    t.last_name_en
                ) AS teacher_name,

                s.classroom_id,
                c.classroom_name,

                s.academic_year_id,

                s.day_of_week,
                s.period_number,
                s.start_time,
                s.end_time
FROM schedules s

            JOIN class_sections cs
                ON cs.section_id = s.section_id

            JOIN subjects sub
                ON sub.subject_id = cs.subject_id

            JOIN homeroom_classes hc
                ON hc.class_id = s.homeroom_class_id

            JOIN teachers t
                ON t.teacher_id = cs.teacher_id

            JOIN classrooms c
                ON c.classroom_id = s.classroom_id

            JOIN enrollments e
                ON e.homeroom_class_id = s.homeroom_class_id
                AND e.academic_year_id = s.academic_year_id

            WHERE e.student_id = ?
              AND e.academic_year_id = ?

            ORDER BY
                CASE s.day_of_week
                    WHEN 'MONDAY' THEN 1
                    WHEN 'TUESDAY' THEN 2
                    WHEN 'WEDNESDAY' THEN 3
                    WHEN 'THURSDAY' THEN 4
                    WHEN 'FRIDAY' THEN 5
                    WHEN 'SATURDAY' THEN 6
                END,
                s.period_number
            """;

        return jdbcTemplate.query(
                sql,
                scheduleMapper,
                studentId,
                academicYearId
        );
    }


    public List<ScheduleResponse> getAllSchedules(Integer academicYearId) {

        String sql = """
        SELECT
            s.schedule_id,
            s.section_id,
            s.homeroom_class_id,
            s.classroom_id,
            s.academic_year_id,
            s.day_of_week,
            s.period_number,
            s.start_time,
            s.end_time,

            cs.section_code,
            c.classroom_name,
            hc.class_name

        FROM schedules s

        JOIN class_sections cs
            ON cs.section_id = s.section_id

        JOIN classrooms c
            ON c.classroom_id = s.classroom_id

        JOIN homeroom_classes hc
            ON hc.homeroom_class_id = s.homeroom_class_id

        WHERE s.academic_year_id = ?

        ORDER BY
            s.day_of_week,
            s.period_number,
            hc.class_name,
            c.classroom_name
        """;

        return jdbcTemplate.query(
                sql,
                scheduleMapper,
                academicYearId
        );
    }
}