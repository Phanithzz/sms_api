package com.sms.smsApi.repository;

import com.sms.smsApi.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<Classroom, Long> {

    @Query("""
        SELECT COUNT(c)
        FROM Classroom c
    """)
    long count();
}
