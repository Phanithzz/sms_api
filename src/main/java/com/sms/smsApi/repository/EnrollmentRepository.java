package com.sms.smsApi.repository;

import com.sms.smsApi.dto.EnrollmentResponse;
import com.sms.smsApi.dto.requestDto.EnrollSearchFilter;
import com.sms.smsApi.dto.requestDto.EnrollmentRequest;
import com.sms.smsApi.dto.requestDto.GradeEnrollmentCount;
import com.sms.smsApi.dto.requestDto.SectionResponse;
import com.sms.smsApi.model.enums.EnrollmentStatus;
import com.sms.smsApi.rowMapper.EnrollmentRowmapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<EnrollmentResponse> enrollmentMapper =
            new EnrollmentRowmapper();


    // =========================================================
    // CHECK IF STUDENT ALREADY ENROLLED
    // =========================================================

    public boolean exists(
            String studentId,
            Integer academicYearId) {

        Integer count = jdbc.queryForObject(
                """
                SELECT COUNT(*)
                FROM enrollments
                WHERE student_id = ?
                  AND academic_year_id = ?
                """,
                Integer.class,
                studentId,
                academicYearId
        );

        return count != null && count > 0;
    }


    // =========================================================
    // INSERT ENROLLMENT
    // =========================================================

    public Integer insert(
            EnrollmentRequest request) {

        String sql = """
                INSERT INTO enrollments (
                    student_id,
                    homeroom_class_id,
                    academic_year_id,
                    status,
                    enrolled_at
                )
                VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
                RETURNING enrollment_id
                """;

        return jdbc.queryForObject(
                sql,
                Integer.class,
                request.getStudentId(),
                request.getHomeroomClassId(),
                request.getAcademicYearId(),
                EnrollmentStatus.ACTIVE.name()
        );
    }


    // =========================================================
    // FIND BY ID
    // =========================================================

    public Optional<EnrollmentResponse> findById(
            Integer id) {

        String enrollmentSql = """
            SELECT
                enrollment_id,
                student_id,
                homeroom_class_id,
                academic_year_id,
                status,
                enrolled_at
            FROM enrollments
            WHERE enrollment_id = ?
            """;

        List<EnrollmentResponse> enrollments =
                jdbc.query(
                        enrollmentSql,
                        enrollmentMapper,
                        id
                );

        if (enrollments.isEmpty()) {
            return Optional.empty();
        }

        EnrollmentResponse enrollment =
                enrollments.get(0);


        String sectionSql = """
            SELECT
                section_id,
                section_code,
                subject_id,
                homeroom_class_id,
                teacher_id,
                classroom_id,
                academic_year_id,
                semester,
                shift,
                enrolled_count,
                max_capacity
            FROM class_sections
            WHERE homeroom_class_id = ?
              AND academic_year_id = ?
            ORDER BY section_id
            """;

        List<SectionResponse> sections =
                jdbc.query(
                        sectionSql,
                        (rs, rowNum) -> {

                            SectionResponse section =
                                    new SectionResponse();

                            section.setSectionId(
                                    rs.getInt("section_id"));

                            section.setSectionCode(
                                    rs.getString("section_code"));

                            section.setSubjectId(
                                    rs.getInt("subject_id"));

                            section.setHomeroomClassId(
                                    rs.getString(
                                            "homeroom_class_id"));

                            section.setTeacherId(
                                    rs.getString("teacher_id"));

                            section.setClassroomId(
                                    rs.getInt("classroom_id"));

                            section.setAcademicYearId(
                                    rs.getInt("academic_year_id"));

                            section.setSemester(
                                    rs.getInt("semester"));

                            section.setShift(
                                    rs.getString("shift"));

                            section.setEnrolledCount(
                                    rs.getInt(
                                            "enrolled_count"));

                            section.setMaxCapacity(
                                    rs.getInt(
                                            "max_capacity"));

                            return section;
                        },
                        enrollment.getHomeroomClassId(),
                        enrollment.getAcademicYearId()
                );


        enrollment.setSections(sections);

        return Optional.of(enrollment);
    }
    // =========================================================
    // FIND ACTIVE ENROLLMENTS FOR STUDENT
    // =========================================================

    public List<EnrollmentResponse>
    findActiveByStudent(
            String studentId) {

        String sql = """
                SELECT
                    enrollment_id,
                    student_id,
                    homeroom_class_id,
                    academic_year_id,
                    status,
                    enrolled_at
                FROM enrollments
                WHERE student_id = ?
                  AND status = ?
                ORDER BY enrollment_id DESC
                """;

        return jdbc.query(
                sql,
                enrollmentMapper,
                studentId,
                EnrollmentStatus.ACTIVE.name()
        );
    }


    // =========================================================
    // FIND STUDENT + ACADEMIC YEAR
    // =========================================================

    public Optional<EnrollmentResponse>
    findByStudentAndAcademicYear(
            String studentId,
            Integer academicYearId) {

        String sql = """
                SELECT
                    enrollment_id,
                    student_id,
                    homeroom_class_id,
                    academic_year_id,
                    status,
                    enrolled_at
                FROM enrollments
                WHERE student_id = ?
                  AND academic_year_id = ?
                """;

        List<EnrollmentResponse> list =
                jdbc.query(
                        sql,
                        enrollmentMapper,
                        studentId,
                        academicYearId
                );

        return list.stream().findFirst();
    }


    // =========================================================
    // UPDATE STATUS
    // =========================================================

    public void updateStatus(
            Integer id,
            EnrollmentStatus status) {

        jdbc.update(
                """
                UPDATE enrollments
                SET status = ?
                WHERE enrollment_id = ?
                """,
                status.name(),
                id
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    public void delete(Integer id) {

        jdbc.update(
                """
                DELETE FROM enrollments
                WHERE enrollment_id = ?
                """,
                id
        );
    }


    // =========================================================
    // SEARCH
    // =========================================================

    public Page<EnrollmentResponse> search(
            EnrollSearchFilter filter) {

        StringBuilder sql = new StringBuilder("""
            SELECT
                enrollment_id,
                student_id,
                homeroom_class_id,
                academic_year_id,
                status,
                enrolled_at
            FROM enrollments
            WHERE status = 'ACTIVE' 
            """);

        List<Object> params = new ArrayList<>();


        // =====================================================
        // STUDENT
        // =====================================================
        if (filter.getKeyword() != null
                && !filter.getKeyword().isBlank()) {

            sql.append("""
            AND (
                CAST(enrollment_id AS VARCHAR) LIKE ?
                OR student_id LIKE ?
            )
            """);

            String keyword = "%" + filter.getKeyword().trim() + "%";

            params.add(keyword);
            params.add(keyword);
        }
        if (filter.getStudentId() != null
                && !filter.getStudentId().isBlank()) {

            sql.append(" AND student_id = ?");
            params.add(filter.getStudentId());
        }


        // =====================================================
        // HOMEROOM CLASS
        // =====================================================

        if (filter.getHomeroomClassId() != null) {

            sql.append(" AND homeroom_class_id = ?");
            params.add(filter.getHomeroomClassId());
        }


        // =====================================================
        // ACADEMIC YEAR
        // =====================================================

        if (filter.getAcademicYearId() != null) {

            sql.append(" AND academic_year_id = ?");
            params.add(filter.getAcademicYearId());
        }


        // =====================================================
        // STATUS
        // =====================================================

        if (filter.getStatus() != null) {

            sql.append(" AND status = ?");
            params.add(filter.getStatus().name());
        }


        // =====================================================
        // PAGINATION
        // =====================================================

        int page = filter.getPage() != null
                ? filter.getPage()
                : 0;

        int size = filter.getSize() != null
                ? filter.getSize()
                : 10;

        int offset = page * size;


        sql.append(" ORDER BY enrollment_id DESC");
        sql.append(" LIMIT ? OFFSET ?");

        params.add(size);
        params.add(offset);


        // =====================================================
        // DATA
        // =====================================================

        List<EnrollmentResponse> content =
                jdbc.query(
                        sql.toString(),
                        enrollmentMapper,
                        params.toArray()
                );


        // =====================================================
        // COUNT
        // =====================================================

        StringBuilder countSql =
                new StringBuilder("""
                    SELECT COUNT(*)
                    FROM enrollments
                    WHERE status = 'ACTIVE'
                    """);

        List<Object> countParams =
                new ArrayList<>();

        if (filter.getKeyword() != null
                && !filter.getKeyword().isBlank()) {

            countSql.append("""
            AND (
                CAST(enrollment_id AS VARCHAR) LIKE ?
                OR student_id LIKE ?
            )
            """);

            String keyword = "%" + filter.getKeyword().trim() + "%";

            countParams.add(keyword);
            countParams.add(keyword);
        }
        // Student
        if (filter.getStudentId() != null
                && !filter.getStudentId().isBlank()) {

            countSql.append(" AND student_id = ?");
            countParams.add(filter.getStudentId());
        }


        // Homeroom Class
        if (filter.getHomeroomClassId() != null) {

            countSql.append(" AND homeroom_class_id = ?");
            countParams.add(filter.getHomeroomClassId());
        }


        // Academic Year
        if (filter.getAcademicYearId() != null) {

            countSql.append(" AND academic_year_id = ?");
            countParams.add(filter.getAcademicYearId());
        }


        // Status
        if (filter.getStatus() != null) {

            countSql.append(" AND status = ?");
            countParams.add(filter.getStatus().name());
        }


        Long total =
                jdbc.queryForObject(
                        countSql.toString(),
                        Long.class,
                        countParams.toArray()
                );


        // =====================================================
        // RETURN PAGE
        // =====================================================

        return new PageImpl<>(
                content,
                PageRequest.of(page, size),
                total != null ? total : 0
        );
    }

    public List<GradeEnrollmentCount> countEnrollmentByGrade() {

        String sql = """
            SELECT
                hc.grade_level,
                COUNT(e.enrollment_id) AS student_count
            FROM enrollments e
            JOIN homeroom_classes hc
                ON hc.class_id = e.homeroom_class_id
            WHERE e.status = 'ACTIVE'
            GROUP BY hc.grade_level
            ORDER BY hc.grade_level
            """;

        return jdbc.query(
                sql,
                (rs, rowNum) ->
                        new GradeEnrollmentCount(
                                rs.getInt("grade_level"),
                                rs.getLong("student_count")
                        )
        );
    }

}