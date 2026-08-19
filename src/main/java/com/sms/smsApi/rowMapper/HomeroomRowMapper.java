package com.sms.smsApi.rowMapper;

import com.sms.smsApi.model.HomeroomClass;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class HomeroomRowMapper
        implements RowMapper<HomeroomClass> {

    @Override
    public HomeroomClass mapRow(
            ResultSet rs,
            int rowNum) throws SQLException {

        HomeroomClass homeroomClass =
                new HomeroomClass();

        homeroomClass.setClassId(
                rs.getInt("class_id"));

        homeroomClass.setClassCode(
                rs.getString("class_code"));

        homeroomClass.setGradeLevel(
                (Integer) rs.getObject("grade_level"));

        homeroomClass.setShift(
                rs.getString("shift"));

        homeroomClass.setHomeroomTeacherId(
                rs.getString("homeroom_teacher_id"));

        homeroomClass.setClassroomId(
                (Integer) rs.getObject("classroom_id"));

        homeroomClass.setAcademicYearId(
                (Integer) rs.getObject("academic_year_id"));

        homeroomClass.setMaxCapacity(
                (Integer) rs.getObject("max_capacity"));

        homeroomClass.setEnrolledCount(
                rs.getInt("enrolled_count"));

        if (rs.getTimestamp("created_at") != null) {
            homeroomClass.setCreatedAt(
                    Timestamp.valueOf(rs.getTimestamp("created_at")
                            .toLocalDateTime()));
        }

        if (rs.getTimestamp("updated_at") != null) {
            homeroomClass.setUpdatedAt(
                    Timestamp.valueOf(rs.getTimestamp("updated_at")
                            .toLocalDateTime()));
        }

        return homeroomClass;
    }
}