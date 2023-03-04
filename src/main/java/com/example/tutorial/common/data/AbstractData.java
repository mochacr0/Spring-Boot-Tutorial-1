package com.example.tutorial.common.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@NoArgsConstructor
public abstract class AbstractData {
    protected UUID id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
