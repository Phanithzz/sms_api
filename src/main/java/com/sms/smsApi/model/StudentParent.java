package com.sms.smsApi.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "student_parents",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_student_parent",
                        columnNames = {"student_id", "parent_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentParent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "parent_id", nullable = false)
    private String parentId;

    @Column(name = "relationship_type")
    private String relationshipType;

    @Column(name = "is_primary_contact")
    private Boolean isPrimaryContact;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}