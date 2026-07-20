package com.sms.smsApi.rowMapper;

import com.sms.smsApi.model.AcademicYear;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
@Component
public class AcademicYearRowMapper implements RowMapper<AcademicYear> {
    @Override
    public AcademicYear mapRow(ResultSet rs, int rowNum) throws SQLException {
        AcademicYear ay = new AcademicYear();
        ay.setAcademicYearId(rs.getLong("id"));
        ay.setYearName(rs.getString("year_name"));
        ay.setStartDate(rs.getDate("start_date").toLocalDate());
        ay.setEndDate(rs.getDate("end_date").toLocalDate());
        ay.setIsCurrent(rs.getBoolean("is_current"));
        ay.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return ay;
    }
}
