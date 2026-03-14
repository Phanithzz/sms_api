package com.sms.smsApi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "request_maps")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "http_methods")
    private String httpMethod;

    @Column(name = "url")
    private String url;

    @Column(name = "roles")
    private String roles;

    @Column(name = "is_enabled")
    private Boolean isEnabled;
    @Column(name = "created_at")
    private Timestamp createdAt= new Timestamp(System.currentTimeMillis());
    @Column(name = "updated_at")
    private Timestamp updatedAt= new Timestamp(System.currentTimeMillis());


}
