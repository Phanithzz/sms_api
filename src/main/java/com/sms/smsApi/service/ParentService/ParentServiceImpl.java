package com.sms.smsApi.service.ParentService;

import com.sms.smsApi.dto.requestDto.ParentRequest;
import com.sms.smsApi.dto.requestDto.ParentResponse;
import com.sms.smsApi.dto.requestDto.ParentSearchFilter;
import com.sms.smsApi.dto.requestDto.StudentRequestFilter;
import com.sms.smsApi.exception.ResourceNotFoundException;
import com.sms.smsApi.model.Parent;
import com.sms.smsApi.repository.ParentRepository;
import com.sms.smsApi.service.UserService.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ParentServiceImpl implements ParentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ParentRepository parentRepository;
    public ParentServiceImpl(NamedParameterJdbcTemplate jdbcTemplate, ParentRepository parentRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.parentRepository = parentRepository;
    }
    public Map<String, Object> getParent(ParentSearchFilter req) {

        StringBuilder sql = new StringBuilder("""
            SELECT * FROM parents
            WHERE created_at IS NOT NULL
        """);

        StringBuilder countSql = new StringBuilder("""
            SELECT COUNT(*) FROM parents
               WHERE created_at IS NOT NULL
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        // 🔍 SEARCH
        if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
            sql.append("""
                AND (
                    LOWER(father_name_en) LIKE :keyword
                    OR LOWER(mother_name_en) LIKE :keyword
                    OR parent_id LIKE :keyword
                )
            """);

            countSql.append("""
                AND (
                    LOWER(father_name_en) LIKE :keyword
                    OR LOWER(mother_name_en) LIKE :keyword
                    OR parent_id LIKE :keyword
                )
            """);

            params.addValue("keyword", "%" + req.getKeyword().toLowerCase() + "%");
        }

        if (req.getProvince() != null && !req.getProvince().isEmpty()) {
            sql.append(" AND province = :province");
            countSql.append(" AND province = :province");
            params.addValue("province", req.getProvince());
        }


        //  (prevent SQL injection)
        List<String> allowedSortFields = List.of(
                "parent_id", "father_name_en"
                ,"mother_name_en", "province", "created_at",
                "user_id"
        );

        String sortBy = allowedSortFields.contains(req.getSortBy())
                ? req.getSortBy()
                : "parent_id";

        String direction = req.getDirection().equalsIgnoreCase("desc") ? "DESC" : "ASC";

        sql.append(" ORDER BY ").append(sortBy).append(" ").append(direction);

        sql.append(" LIMIT :limit OFFSET :offset");
        params.addValue("limit", req.getSize());
        params.addValue("offset", req.getPage() * req.getSize());


        List<Parent> parents = jdbcTemplate.query(
                sql.toString(),
                params,
                (rs, rowNum) -> {

                    Parent p = new Parent();

                    // Parent
                    p.setParentId(rs.getString("parent_id"));
                    p.setUserId(rs.getInt("user_id"));

                    // Parent own name
                    p.setFirstNameEn(rs.getString("first_name_en"));
                    p.setLastNameEn(rs.getString("last_name_en"));
                    p.setFirstNameKh(rs.getString("first_name_kh"));
                    p.setLastNameKh(rs.getString("last_name_kh"));

                    // Father
                    p.setFatherNameKh(rs.getString("father_name_kh"));
                    p.setFatherNameEn(rs.getString("father_name_en"));
                    p.setFatherPhone(rs.getString("father_phone"));
                    p.setFatherJob(rs.getString("father_job"));

                    if (rs.getDate("father_dob") != null) {
                        p.setFatherDob(
                                rs.getDate("father_dob").toLocalDate()
                        );
                    }

                    // Mother
                    p.setMotherNameKh(rs.getString("mother_name_kh"));
                    p.setMotherNameEn(rs.getString("mother_name_en"));
                    p.setMotherPhone(rs.getString("mother_phone"));
                    p.setMotherJob(rs.getString("mother_job"));

                    if (rs.getDate("mother_dob") != null) {
                        p.setMotherDob(
                                rs.getDate("mother_dob").toLocalDate()
                        );
                    }

                    // Address
                    p.setCurrentAddress(rs.getString("current_address"));
                    p.setProvince(rs.getString("province"));

                    // Audit fields
                    p.setCreatedAt(
                            rs.getTimestamp("created_at") != null
                                    ? rs.getTimestamp("created_at").toLocalDateTime()
                                    : null
                    );

                    p.setUpdatedAt(
                            rs.getTimestamp("updated_at") != null
                                    ? rs.getTimestamp("updated_at").toLocalDateTime()
                                    : null
                    );

                    return p;
                }
        );

        LOGGER.info("SQL: {} / count_SQL: {}", sql, countSql);

        Integer total = jdbcTemplate.queryForObject(
                countSql.toString(),
                params,
                Integer.class
        );

        Map<String, Object> result = new HashMap<>();
        result.put("data", parents);
        result.put("total", total);
        result.put("page", req.getPage());
        result.put("size", req.getSize());

        return result;
    }

    @Override
    public Parent updateParent(String parentId, ParentRequest request) {

        Parent existing = parentRepository.findParentById(parentId);

        if (existing == null) {
            throw new RuntimeException(
                    "Parent not found with id: " + parentId
            );
        }

        // User
        //existing.setUserId(request.getUserId());

        // Parent name
        existing.setFirstNameEn(request.getFirstNameEn());
        existing.setLastNameEn(request.getLastNameEn());
        existing.setFirstNameKh(request.getFirstNameKh());
        existing.setLastNameKh(request.getLastNameKh());

        // Father
        existing.setFatherNameKh(request.getFatherNameKh());
        existing.setFatherNameEn(request.getFatherNameEn());
        existing.setFatherPhone(request.getFatherPhone());
        existing.setFatherJob(request.getFatherJob());
        existing.setFatherDob(request.getFatherDob());

        // Mother
        existing.setMotherNameKh(request.getMotherNameKh());
        existing.setMotherNameEn(request.getMotherNameEn());
        existing.setMotherPhone(request.getMotherPhone());
        existing.setMotherJob(request.getMotherJob());
        existing.setMotherDob(request.getMotherDob());

        // Address
        existing.setCurrentAddress(request.getCurrentAddress());
        existing.setProvince(request.getProvince());

        return parentRepository.save(existing);
    }

    public List<ParentResponse> findAll() {
        return parentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ParentResponse findById(String id) {
        return toResponse(getOrThrow(id));
    }

    public void delete(String id) {
        getOrThrow(id);
        parentRepository.deleteById(id);
    }
    private Parent getOrThrow(String id) {
        return parentRepository.findById(id)
                .orElseThrow(() ->
                        ResourceNotFoundException.of("Parent", id)
                );
    }

    private ParentResponse toResponse(Parent p) {

        return new ParentResponse(
                p.getParentId(),
                p.getUserId(),

                // Father
                p.getFatherNameKh(),
                p.getFatherNameEn(),
                p.getFatherPhone(),
                p.getFatherJob(),
                p.getFatherDob(),

                // Mother
                p.getMotherNameKh(),
                p.getMotherNameEn(),
                p.getMotherPhone(),
                p.getMotherJob(),
                p.getMotherDob(),

                // Address
                p.getCurrentAddress(),
                p.getProvince(),

                // Parent name
                p.getFirstNameEn(),
                p.getLastNameEn(),
                p.getFirstNameKh(),
                p.getLastNameKh(),

                // Audit
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}

