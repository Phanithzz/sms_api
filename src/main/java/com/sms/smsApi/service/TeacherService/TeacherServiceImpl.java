package com.sms.smsApi.service.TeacherService;

import com.sms.smsApi.dto.requestDto.StudentRequestFilter;
import com.sms.smsApi.dto.requestDto.TeacherRequestFilter;
import com.sms.smsApi.model.Student;
import com.sms.smsApi.model.Teacher;
import com.sms.smsApi.service.UserService.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeacherServiceImpl implements TeacherService{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TeacherServiceImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Object> getTeachers(TeacherRequestFilter req) {

        StringBuilder sql = new StringBuilder("""
            SELECT * FROM teachers
            WHERE deleted_at IS NULL
        """);

        StringBuilder countSql = new StringBuilder("""
            SELECT COUNT(*) FROM teachers
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

        if (req.getDepartmentId() != null && req.getDepartmentId() > 0) {
            sql.append(" AND department_id = :departmentId");
            countSql.append(" AND department_id = :departmentId");
            params.addValue("departmentId", req.getDepartmentId());
        }
        if (req.getSex() != null && !req.getSex().isEmpty()) {
            sql.append(" AND sex = :from");
            countSql.append(" AND sex = :from");
            params.addValue("from", req.getSex());
        }
        if (req.getHiredDate() != null) {
            sql.append(" AND hired_date >= :from");
            countSql.append(" AND hired_date >= :from");
            params.addValue("from", req.getHiredDate());
        }

        List<String> allowedSortFields = List.of(
                "teacher_id", "full_name_en"
                ,"full_name_kh", "sex","department_id", "created_at",
                "hired_date"
        );

        String sortBy = allowedSortFields.contains(req.getSortBy())
                ? req.getSortBy()
                : "teacher_id";

        String direction = req.getDirection().equalsIgnoreCase("desc") ? "DESC" : "ASC";

        sql.append(" ORDER BY ").append(sortBy).append(" ").append(direction);

        // 📄 PAGINATION
        sql.append(" LIMIT :limit OFFSET :offset");
        params.addValue("limit", req.getSize());
        params.addValue("offset", req.getPage() * req.getSize());

        // 🚀 EXECUTE
        List<Teacher> teachers = jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            Teacher s = new Teacher();
            s.setTeacherId(rs.getString("teacher_id"));
            s.setFullNameEn(rs.getString("full_name_en"));
            s.setFullNameKh(rs.getString("full_name_kh"));
            s.setPhoneNumber(rs.getString("phone_number"));
            s.setDepartmentId(rs.getInt("department_id"));
            s.setSex(rs.getString("sex"));
            return s;
        });

        LOGGER.info(" SQL :{}/ count_SQL: {}",sql,countSql);
        Integer total = jdbcTemplate.queryForObject(countSql.toString(), params, Integer.class);

        Map<String, Object> result = new HashMap<>();
        result.put("data", teachers);
        result.put("total", total);
        result.put("page", req.getPage());
        result.put("size", req.getSize());

        return result;
    }
}
