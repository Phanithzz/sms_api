package com.sms.smsApi.repository;

import com.sms.smsApi.dto.EnrollmentResponse;
import com.sms.smsApi.dto.requestDto.EnrollmentRequest;
import com.sms.smsApi.dto.requestDto.SectionResponse;
import com.sms.smsApi.model.enums.EnrollmentStatus;
import com.sms.smsApi.rowMapper.EnrollmentRowmapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
            String studentId,
            Integer homeroomClassId,
            Integer academicYearId,
            EnrollmentStatus status,
            Pageable pageable) {

        StringBuilder sql = new StringBuilder("""
                SELECT
                    enrollment_id,
                    student_id,
                    homeroom_class_id,
                    academic_year_id,
                    status,
                    enrolled_at
                FROM enrollments
                WHERE 1 = 1
                """);

        List<Object> params =
                new ArrayList<>();


        // Student
        if (studentId != null
                && !studentId.isBlank()) {

            sql.append(
                    " AND student_id = ?");

            params.add(studentId);
        }


        // Homeroom Class
        if (homeroomClassId != null) {

            sql.append(
                    " AND homeroom_class_id = ?");

            params.add(homeroomClassId);
        }


        // Academic Year
        if (academicYearId != null) {

            sql.append(
                    " AND academic_year_id = ?");

            params.add(academicYearId);
        }


        // Status
        if (status != null) {

            sql.append(
                    " AND status = ?");

            params.add(status.name());
        }


        // Pagination
        sql.append(
                " ORDER BY enrollment_id DESC");

        sql.append(
                " LIMIT ? OFFSET ?");

        params.add(
                pageable.getPageSize());

        params.add(
                pageable.getOffset());


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
                        WHERE 1 = 1
                        """);

        List<Object> countParams =
                new ArrayList<>();


        if (studentId != null
                && !studentId.isBlank()) {

            countSql.append(
                    " AND student_id = ?");

            countParams.add(studentId);
        }


        if (homeroomClassId != null) {

            countSql.append(
                    " AND homeroom_class_id = ?");

            countParams.add(homeroomClassId);
        }


        if (academicYearId != null) {

            countSql.append(
                    " AND academic_year_id = ?");

            countParams.add(academicYearId);
        }


        if (status != null) {

            countSql.append(
                    " AND status = ?");

            countParams.add(status.name());
        }


        Long total =
                jdbc.queryForObject(
                        countSql.toString(),
                        Long.class,
                        countParams.toArray()
                );


        return new PageImpl<>(
                content,
                pageable,
                total != null ? total : 0
        );
    }
}