package com.sms.smsApi.rowMapper;

import com.sms.smsApi.dto.EnrollmentResponse;
import com.sms.smsApi.model.enums.EnrollmentStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class EnrollmentRowmapper implements RowMapper<EnrollmentResponse> {
    @Override
    public EnrollmentResponse mapRow(
            ResultSet rs,
            int rowNum) throws SQLException {

        EnrollmentResponse response =
                new EnrollmentResponse();

        response.setEnrollmentId(
                rs.getInt("enrollment_id"));

        response.setStudentId(
                rs.getString("student_id"));

        response.setHomeroomClassId(
                rs.getInt("homeroom_class_id"));

        response.setAcademicYearId(
                rs.getInt("academic_year_id"));

        response.setStatus(
                EnrollmentStatus.valueOf(
                        rs.getString("status")
                                .toUpperCase()
                ));

        if (rs.getTimestamp("enrolled_at") != null) {

            response.setEnrolledAt(
                    LocalDate.from(rs.getTimestamp("enrolled_at")
                            .toLocalDateTime())
            );
        }

        return response;
    }
}