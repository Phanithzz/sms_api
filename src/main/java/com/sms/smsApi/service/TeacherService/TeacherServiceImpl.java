package com.sms.smsApi.service.TeacherService;


import com.sms.smsApi.dto.requestDto.TeacherRequest;
import com.sms.smsApi.dto.requestDto.TeacherRequestFilter;
import com.sms.smsApi.dto.requestDto.TeacherResponse;
import com.sms.smsApi.dto.requestDto.TeacherSearchResponse;
import com.sms.smsApi.exception.DuplicateResourceException;
import com.sms.smsApi.exception.ResourceNotFoundException;
import com.sms.smsApi.model.Teacher;
import com.sms.smsApi.model.enums.TeacherStatus;
import com.sms.smsApi.repository.TeacherRepository;
import com.sms.smsApi.service.UserService.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl implements TeacherService{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TeacherRepository repository;

    public TeacherServiceImpl(NamedParameterJdbcTemplate jdbcTemplate, TeacherRepository repository) {
        this.jdbcTemplate = jdbcTemplate;
        this.repository = repository;
    }

    @Override
    public Map<String, Object> getTeachers(TeacherRequestFilter req) {

        StringBuilder sql = new StringBuilder("""
            SELECT  t.*, d.department_code, d.department_name_en,d.department_name_kh FROM teachers t
            LEFT JOIN departments d ON t.department_id = d.department_id
            WHERE t.deleted_at IS NULL
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
                    LOWER(t.full_name_en) LIKE :keyword
                    OR LOWER(t.full_name_kh) LIKE :keyword
                    OR t.phone_number LIKE :keyword
                    OR t.teacher_id LIKE :keyword
                )
            """);

            countSql.append("""
                AND (
                    LOWER(full_name_en) LIKE :keyword
                    OR LOWER(full_name_kh) LIKE :keyword
                    OR phone_number LIKE :keyword
                    OR teacher_id LIKE :keyword
                )
            """);

            params.addValue("keyword", "%" + req.getKeyword().toLowerCase() + "%");
        }

        if (req.getDepartmentId() != null && req.getDepartmentId() > 0) {
            sql.append(" AND t.department_id = :departmentId");
            countSql.append(" AND department_id = :departmentId");
            params.addValue("departmentId", req.getDepartmentId());
        }
        if (req.getSex() != null && !req.getSex().isEmpty()) {
            sql.append(" AND t.sex = :from");
            countSql.append(" AND sex = :from");
            params.addValue("from", req.getSex());
        }
        if (req.getHiredDate() != null) {
            sql.append(" AND t.hired_date >= :from");
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

        sql.append(" LIMIT :limit OFFSET :offset");
        params.addValue("limit", req.getSize());
        params.addValue("offset", req.getPage() * req.getSize());

        List<TeacherSearchResponse> teachers = jdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            TeacherSearchResponse t = new TeacherSearchResponse();
            String status = rs.getString("employment_status");
            t.setEmploymentStatus(status == null ? null : TeacherStatus.fromDatabase(status));
            t.setTeacherId(rs.getString("teacher_id"));
            t.setUserId(rs.getInt("user_id"));
            t.setFullNameKh(rs.getString("full_name_kh"));
            t.setFullNameEn(rs.getString("full_name_en"));
            t.setFirstNameEn(rs.getString("first_name_en"));
            t.setLastNameEn(rs.getString("last_name_en"));
            t.setFirstNameKh(rs.getString("first_name_kh"));
            t.setLastNameKh(rs.getString("last_name_kh"));
            t.setSex(rs.getString("sex"));
            t.setDateOfBirth(rs.getDate("date_of_birth") != null
                    ? Date.valueOf(rs.getDate("date_of_birth").toLocalDate())
                    : null);
            t.setPhoneNumber(rs.getString("phone_number"));
            t.setEmail(rs.getString("email"));
            t.setNationalId(rs.getString("national_id"));
            t.setDepartmentId(rs.getInt("department_id"));
            t.setSpecialization(rs.getString("specialization"));
            t.setQualification(rs.getString("qualification"));
            t.setHiredDate(rs.getDate("hired_date") != null
                    ? rs.getDate("hired_date").toLocalDate()
                    : null);
            t.setSalary(rs.getBigDecimal("salary"));
            t.setProfilePhoto(rs.getString("profile_photo"));

            t.setCreatedAt(rs.getTimestamp("created_at") != null
                    ? rs.getTimestamp("created_at").toLocalDateTime()
                    : null);
            t.setUpdatedAt(rs.getTimestamp("updated_at") != null
                    ? rs.getTimestamp("updated_at").toLocalDateTime()
                    : null);
            t.setDeletedAt(rs.getTimestamp("deleted_at") != null
                    ? rs.getTimestamp("deleted_at").toLocalDateTime()
                    : null);
            t.setDepartmentCode(rs.getString("department_code"));
            t.setDepartmentNameEn(rs.getString("department_name_en"));
            t.setDepartmentNameKh(rs.getString("department_name_kh"));
            return t;
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


    public List<TeacherResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TeacherResponse findById(String id) {
        return toResponse(getOrThrow(id));
    }

    public TeacherResponse create(TeacherRequest request) {


        if (repository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already exists: " + request.getEmail());
        }

        Teacher teacher = new Teacher();
        teacher.setTeacherId(generateTeacherID());
        teacher.setFirstNameEn(request.getFirstNameEn());
        teacher.setLastNameEn(request.getLastNameEn());
        teacher.setFirstNameKh(request.getFirstNameKh());
        teacher.setLastNameKh(request.getLastNameKh());
        teacher.setSex(request.getSex());
        teacher.setEmail(request.getEmail());
        teacher.setDepartmentId(request.getDepartmentId());
        teacher.setQualification(request.getQualification());
        teacher.setSpecialization(request.getSpecialization());
        teacher.setPhoneNumber(request.getPhoneNumber());
        teacher.setHiredDate(request.getHiredDate());
        teacher.setDateOfBirth(request.getDateOfBirth());
        teacher.setSalary(request.getSalary());

        return toResponse(repository.save(teacher));
    }

    public TeacherResponse update(String id, TeacherRequest request) {

        Teacher teacher = getOrThrow(id);
        // Check duplicate email
        if (!teacher.getEmail().equals(request.getEmail())
                && repository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already exists: " + request.getEmail());
        }

        //teacher.setEmployeeNumber(request.getEmployeeNumber());
        //teacher.setTeacherId(generateTeacherID());
        teacher.setFullNameEn(request.getFirstNameEn() + ' ' + request.getLastNameEn());
        teacher.setFullNameKh(request.getFirstNameKh() + ' ' + request.getLastNameKh());
        teacher.setFirstNameEn(request.getFirstNameEn());
        teacher.setLastNameEn(request.getLastNameEn());
        teacher.setFirstNameKh(request.getFirstNameKh());
        teacher.setLastNameKh(request.getLastNameKh());
        teacher.setEmploymentStatus(request.getEmploymentStatus());
        teacher.setSex(request.getSex());
        teacher.setEmail(request.getEmail());
        teacher.setDepartmentId(request.getDepartmentId());
        teacher.setNationalId(request.getNationalId());
        teacher.setQualification(request.getQualification());
        teacher.setSpecialization(request.getSpecialization());
        teacher.setPhoneNumber(request.getPhoneNumber());
        teacher.setHiredDate(request.getHiredDate());
        teacher.setDateOfBirth(request.getDateOfBirth());
        teacher.setSalary(request.getSalary());
        Teacher updated = repository.save(teacher);

        return toResponse(updated);
    }

    private String generateTeacherID() {
        String latest = repository.findLatestTeacherIdByPrefix("TH");

        if (latest == null) {
            return "TH000001";
        }

        int seq = Integer.parseInt(latest.substring(2)); // remove "TH"
        return String.format("TH%06d", seq + 1);
    }

    public TeacherResponse updateStatus(String id, TeacherStatus status) {

        Teacher teacher = getOrThrow(id);

        teacher.setEmploymentStatus(status);

        Teacher updated = repository.save(teacher);

        return toResponse(updated);
    }

    public void delete(String id) {

        Teacher teacher = getOrThrow(id);

        repository.delete(teacher);
    }

    private Teacher getOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Teacher", id));
    }

    private TeacherResponse toResponse(Teacher teacher) {
        return new TeacherResponse(
                teacher.getTeacherId(),
                teacher.getUserId(),

                teacher.getFullNameKh(),
                teacher.getFullNameEn(),
                teacher.getFirstNameEn(),
                teacher.getLastNameEn(),
                teacher.getFirstNameKh(),
                teacher.getLastNameKh(),
                teacher.getSex(),
                teacher.getDateOfBirth(),
                teacher.getPhoneNumber(),
                teacher.getEmail(),
                teacher.getNationalId(),
                teacher.getDepartmentId(),
                teacher.getSpecialization(),
                teacher.getQualification(),
                teacher.getHiredDate(),
                teacher.getEmploymentStatus(),
                teacher.getSalary(),
                teacher.getProfilePhoto(),
                teacher.getCreatedAt(),
                teacher.getUpdatedAt(),
                teacher.getDeletedAt()
        );
    }
}
