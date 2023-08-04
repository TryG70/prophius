package com.trygod.prophiusassessment.data;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@MappedSuperclass
@Data
public class BaseEntityData {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @CreationTimestamp
    @Column(name="CREATED_DATE")
    private Date creadtedDate;

    @UpdateTimestamp
    @Column(name="UPDATED_DATE")
    private Date updatedDate;
}
