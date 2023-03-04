package com.example.tutorial.model;

import com.example.tutorial.common.data.ToData;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.tutorial.model.ModelConstants.ID_COLUMN;
import static com.example.tutorial.model.ModelConstants.CREATED_AT_COLUMN;
import static com.example.tutorial.model.ModelConstants.UPDATED_AT_COLUMN;


@Data
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class AbstractEntity<T> implements ToData<T>{
    @Id
    @Column(name = ID_COLUMN)
    protected UUID id;
    @CreatedDate
    @Column(name = CREATED_AT_COLUMN, updatable = false)
    protected LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = UPDATED_AT_COLUMN)
    protected LocalDateTime updatedAt;
}
