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
    private int classId;
    @Column(name = "class_code")
    private String classCode ; // 7A, 7B
    @Column(name = "grade_level")
    private int gradeLevel ; // 0-12
    @Column(name = "shift")
    private String shift;
    @Column(name = "homeroom_teacher_id")
    private String homeroomTeacherId ;

    private int classroomId;
    private int academicYearId;
    private int maxCapacity;
    private int enrolledCount ;
    private Timestamp createdAt ;
    private Timestamp updatedAt ;
}
