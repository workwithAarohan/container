package org.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class BaseModel {
    private Long id;
    private String modifiedBy;
    private Timestamp updatedDate;
}