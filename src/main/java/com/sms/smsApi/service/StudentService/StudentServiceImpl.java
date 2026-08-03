package com.sms.smsApi.service.StudentService;

import com.sms.smsApi.dto.requestDto.StudentRequest;
import com.sms.smsApi.dto.requestDto.StudentRequestFilter;
import com.sms.smsApi.dto.requestDto.StudentResponse;
import com.sms.smsApi.exception.ResourceNotFoundException;
import com.sms.smsApi.model.Student;
import com.sms.smsApi.repository.StudentRepository;
import com.sms.smsApi.service.UserService.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final StudentRepository studentRepository;
    public StudentServiceImpl(NamedParameterJdbcTemplate jdbcTemplate, StudentRepository studentRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.studentRepository = studentRepository;
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
                    OR student_id LIKE :keyword
                )
            """);

            countSql.append("""
                AND (
                    LOWER(full_name_en) LIKE :keyword
                    OR LOWER(full_name_kh) LIKE :keyword
                    OR phone_number LIKE :keyword
                    OR student_id LIKE :keyword
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


    @Override
    public Student updateStudent(String studentId,StudentRequest student) {

        Student existing = studentRepository.findStudentById(studentId);

        if (existing == null) {
            throw new RuntimeException(
                    "Student not found with id: " + studentId);
        }

        existing.setStudentFirstNameEn(student.getFirstNameEn());
        existing.setStudentLastNameEn(student.getLastNameEn());
        existing.setStudentFirstNameKh(student.getFirstNameKh());
        existing.setStudentLastNameKh(student.getLastNameKh());

        existing.setFullNameEn(student.getFullNameEn());
        existing.setFullNameKh(student.getFullNameKh());

        existing.setGender(student.getGender());
        existing.setDateOfBirth(student.getDob());
        existing.setPlaceOfBirth(student.getPlaceOfBirth());
        existing.setNationalId(student.getNationalId());

        existing.setPhoneNumber(student.getPhoneNumber());
        existing.setCurrentAddress(student.getCurrentAddress());
        existing.setProvince(student.getProvince());

        existing.setGradeLevel(student.getGradeLevel());
        existing.setHomeroomClassId(student.getHomeroomClassId());

        existing.setEnrolledDate(student.getEnrolledDate());
        existing.setEndDate(student.getEndDate());

        existing.setStatus(String.valueOf(student.getStatus()));
        existing.setGpa(student.getGpa());

        existing.setProfilePhoto(student.getProfilePhoto());

        existing.setEmergencyContactName(student.getEmergencyContactName());
        existing.setEmergencyContactPhone(student.getEmergencyContactPhone());

        return studentRepository.save(existing);
    }



    public List<StudentResponse> findAll() {
        return studentRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public StudentResponse findById(String id) {
        return toResponse(getOrThrow(id));
    }


    public void delete(String id) {
        getOrThrow(id);
        studentRepository.deleteById(id);
    }

    private Student getOrThrow(String id) {
        return studentRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Student", id));
    }
    private StudentResponse toResponse(Student s) {
        return new StudentResponse(
                s.getStudentId(),
                s.getUserId(),
                s.getStudentFirstNameEn(),
                s.getStudentLastNameEn(),
                s.getStudentFirstNameKh(),
                s.getStudentLastNameKh(),
                s.getFullNameEn(),
                s.getFullNameKh(),
                //s.getEmail(),
                s.getGender(),
                s.getDateOfBirth(),
                s.getPlaceOfBirth(),
                s.getNationalId(),
                s.getPhoneNumber(),
                s.getCurrentAddress(),
                s.getProvince(),
                s.getGradeLevel(),
                s.getHomeroomClassId(),
                s.getEnrolledDate(),
                s.getEndDate(),
                s.getStatus(),
                s.getGpa(),
                s.getProfilePhoto(),
                s.getEmergencyContactName(),
                s.getEmergencyContactPhone(),
                s.getCreatedAt(),
                s.getUpdatedAt(),
                s.getDeletedAt()
        );
    }
}
