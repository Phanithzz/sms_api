package com.sms.smsApi.repository;


import com.sms.smsApi.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestMapRepository extends JpaRepository<Permission, Long> {

    @Query("SELECT rm FROM Permission rm WHERE rm.isEnabled = true ORDER BY rm.id")
    List<Permission> findAll();
}
