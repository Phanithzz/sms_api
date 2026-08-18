package com.sms.smsApi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "homeroom_classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeroomClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id", nullable = false, unique = true)
    private int class_id;
    @Column(name = "class_code")
    private String class_code ; // 7A, 7B
    @Column(name = "grade_level")
    private int grade_level ; // 0-12
    @Column(name = "shift")
    private String shift;
    @Column(name = "homeroom_teacher_id")
    private String homeroom_teacher_id ;

    private int classroom_id;
    private int academic_year_id;
    private int max_capacity;
    private int enrolled_count ;
    private Timestamp created_at ;
    private Timestamp updated_at ;
}
