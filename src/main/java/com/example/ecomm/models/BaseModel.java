package com.example.ecomm.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseModel {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id; // Hibernate maps this to BINARY(16) - 16 raw bytes -> BIN_TO_UUID(id)

    private Date createdAt;
    private Date lastUpdatedAt;

    @Enumerated(EnumType.STRING)
    private State state = State.ACTIVE;
}
