package com.sms.smsApi.repository;

import com.sms.smsApi.model.ClassSection;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SectionRepository {

    private final JdbcTemplate jdbc;

    public ClassSection findById(Integer id){

        String sql = """
                SELECT *
                FROM class_sections
                WHERE section_id=?
                """;

        List<ClassSection> list =
                jdbc.query(sql,
                        new BeanPropertyRowMapper<>(ClassSection.class),
                        id);

        return list.isEmpty()
                ? null
                : list.get(0);
    }

    public void incrementEnrollment(Integer id){

        jdbc.update("""
                UPDATE class_sections
                SET enrolled_count=enrolled_count+1
                WHERE section_id=?
                """,id);
    }

}