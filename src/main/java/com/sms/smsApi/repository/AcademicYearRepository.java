package com.sms.smsApi.repository;


import com.sms.smsApi.dto.requestDto.AcademicYearOption;
import com.sms.smsApi.model.AcademicYear;
import com.sms.smsApi.rowMapper.AcademicYearRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class AcademicYearRepository {

    private final JdbcTemplate jdbcTemplate;
    private final AcademicYearRowMapper rowMapper;

    public List<AcademicYearOption> findOptions() {

        String sql = """
            SELECT
                academic_year_id,
                year_name
            FROM academic_years
            WHERE is_current = true
            ORDER BY start_date DESC
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    AcademicYearOption option =
                            new AcademicYearOption();

                    option.setAcademicYearId(
                            rs.getInt("academic_year_id"));

                    option.setName(
                            rs.getString("year_name"));

                    return option;
                }
        );
    }
    public AcademicYearRepository(JdbcTemplate jdbcTemplate, AcademicYearRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    public List<AcademicYear> findAll() {
        return jdbcTemplate.query("SELECT * FROM academic_year ORDER BY start_date DESC", rowMapper);
    }

    public Optional<AcademicYear> findById(Long id) {
        List<AcademicYear> results = jdbcTemplate.query(
                "SELECT * FROM academic_year WHERE id = ?", rowMapper, id);
        return results.stream().findFirst();
    }

    public Optional<AcademicYear> findCurrent() {
        List<AcademicYear> results = jdbcTemplate.query(
                "SELECT * FROM academic_year WHERE is_current = TRUE LIMIT 1", rowMapper);
        return results.stream().findFirst();
    }

    public boolean existsByYearName(String yearName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM academic_year WHERE year_name = ?", Integer.class, yearName);
        return count != null && count > 0;
    }

    public void clearCurrentFlag() {
        jdbcTemplate.update("UPDATE academic_year SET is_current = FALSE WHERE is_current = TRUE");
    }

    public AcademicYear save(AcademicYear ay) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO academic_year (year_name, start_date, end_date, is_current) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ay.getYearName());
            ps.setObject(2, ay.getStartDate());
            ps.setObject(3, ay.getEndDate());
            ps.setBoolean(4, ay.getIsCurrent());
            return ps;
        }, keyHolder);
        ay.setAcademicYearId(keyHolder.getKey().longValue());
        return ay;
    }

    public int update(Long id, AcademicYear ay) {
        return jdbcTemplate.update(
                "UPDATE academic_year SET year_name = ?, start_date = ?, end_date = ?, is_current = ? WHERE id = ?",
                ay.getYearName(), ay.getStartDate(), ay.getEndDate(), ay.getIsCurrent(), id);
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM academic_year WHERE id = ?", id);
    }
}
