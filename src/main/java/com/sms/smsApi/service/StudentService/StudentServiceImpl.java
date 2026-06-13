package com.sms.smsApi.service.StudentService;

import com.sms.smsApi.dto.requestDto.StudentRequestFilter;
import com.sms.smsApi.model.Student;
import com.sms.smsApi.service.UserService.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentServiceImpl implements StudentService{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StudentServiceImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public Map<String, Object> getStudents(StudentRequestFilter req) {

        StringBuilder sql = new StringBuilder("""
            SELECT * FROM students
            WHERE deleted_at IS NULL
        """);

        StringBuilder countSql = new StringBuilder("""
            SELECT COUNT(*) FROM students
            WHERE deleted_at IS NULL
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        // 🔍 SEARCH
        if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
            sql.append("""
                AND (
                    LOWER(full_name_en) LIKE :keyword
                    OR LOWER(full_name_kh) LIKE :keyword
                    OR phone_number LIKE :keyword
                )
            """);

            countSql.append("""
                AND (
                    LOWER(full_name_en) LIKE :keyword
                    OR LOWER(full_name_kh) LIKE :keyword
                    OR phone_number LIKE :keyword
                )
            """);

            params.addValue("keyword", "%" + req.getKeyword().toLowerCase() + "%");
        }

        if (req.getProvince() != null && !req.getProvince().isEmpty()) {
            sql.append(" AND province = :province");
            countSql.append(" AND province = :province");
            params.addValue("province", req.getProvince());
        }
        if (req.getGradeLevel() != null  ) {
            sql.append(" AND grade_level = :gradeLevel");
            countSql.append(" AND grade_level = :gradeLevel");
            params.addValue("gradeLevel", req.getGradeLevel());
        }

        if (req.getClassId() != null) {
            sql.append(" AND homeroom_class_id = :classId");
            countSql.append(" AND homeroom_class_id = :classId");
            params.addValue("classId", req.getClassId());
        }

        if (req.getEnrolledFrom() != null) {
            sql.append(" AND enrolled_date >= :from");
            countSql.append(" AND enrolled_date >= :from");
            params.addValue("from", req.getEnrolledFrom());
        }

        if (req.getEnrolledTo() != null) {
            sql.append(" AND end_date <= :to");
            countSql.append(" AND end_date <= :to");
            params.addValue("to", req.getEnrolledTo());
        }

        //  (IMPORTANT: prevent SQL injection)
        List<String> allowedSortFields = List.of(
                "student_id", "full_name_en"
                ,"full_name_kh", "grade_level","province", "created_at",
                "enrolled_date","end_date"
        );

        String sortBy = allowedSortFields.contains(req.getSortBy())
                ? req.getSortBy()
                : "student_id";

        String direction = req.getDirection().equalsIgnoreCase("desc") ? "DESC" : "ASC";

        sql.append(" ORDER BY ").append(sortBy).append(" ").append(direction);

        // 📄 PAGINATION
        sql.append(" LIMIT :limit OFFSET :offset");
        params.addValue("limit", req.getSize());
        params.addValue("offset", req.getPage() * req.getSize());

        // 🚀 EXECUTE
        List<Student> students = jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            Student s = new Student();
            s.setStudentId(rs.getString("student_id"));
            s.setFullNameEn(rs.getString("full_name_en"));
            s.setFullNameKh(rs.getString("full_name_kh"));
            s.setPhoneNumber(rs.getString("phone_number"));
            s.setProvince(rs.getString("province"));
            s.setGradeLevel(rs.getInt("grade_level"));
            return s;
        });

        LOGGER.info(" SQL :{}/ count_SQL: {}",sql,countSql);
        Integer total = jdbcTemplate.queryForObject(countSql.toString(), params, Integer.class);

        Map<String, Object> result = new HashMap<>();
        result.put("data", students);
        result.put("total", total);
        result.put("page", req.getPage());
        result.put("size", req.getSize());

        return result;
    }
}
