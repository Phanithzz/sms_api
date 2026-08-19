package com.sms.smsApi.rowMapper;
import com.sms.smsApi.dto.requestDto.ScheduleResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScheduleRowMapper implements RowMapper<ScheduleResponse> {

    @Override
    public ScheduleResponse mapRow(
            ResultSet rs,
            int rowNum) throws SQLException {

        ScheduleResponse response =
                new ScheduleResponse();

        response.setScheduleId(
                rs.getInt("schedule_id"));

        // =========================
        // SECTION
        // =========================

        response.setSectionId(
                rs.getInt("section_id"));

        response.setSectionCode(
                rs.getString("section_code"));


        // =========================
        // SUBJECT
        // =========================

        response.setSubjectId(
                rs.getInt("subject_id"));

        response.setSubjectCode(
                rs.getString("subject_code"));

        response.setSubjectNameEn(
                rs.getString("subject_name_en"));

        response.setSubjectNameKh(
                rs.getString("subject_name_kh"));


        // =========================
        // HOMEROOM CLASS
        // =========================

        response.setHomeroomClassId(
                rs.getInt("homeroom_class_id"));

        response.setClassCode(
                rs.getString("class_code"));

        response.setGradeLevel(
                rs.getInt("grade_level"));


        // =========================
        // TEACHER
        // =========================

        response.setTeacherId(
                rs.getString("teacher_id"));

        response.setTeacherName(
                rs.getString("teacher_name"));


        // =========================
        // CLASSROOM
        // =========================

        response.setClassroomId(
                rs.getInt("classroom_id"));

        response.setClassroomName(
                rs.getString("classroom_name"));


        // =========================
        // ACADEMIC YEAR
        // =========================

        response.setAcademicYearId(
                rs.getInt("academic_year_id"));


        // =========================
        // SCHEDULE
        // =========================

        response.setDayOfWeek(
                rs.getString("day_of_week"));

        response.setPeriodNumber(
                rs.getInt("period_number"));

        if (rs.getTime("start_time") != null) {
            response.setStartTime(
                    rs.getTime("start_time").toLocalTime()
            );
        }

        if (rs.getTime("end_time") != null) {
            response.setEndTime(
                    rs.getTime("end_time").toLocalTime()
            );
        }

        return response;
    }
}
