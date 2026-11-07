package com.example.ecomm.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = @Index(name = "idx_col_category", columnList = "category_id"))
public class Product extends BaseModel{
    private String name;
    private String description;
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    private String imageUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
}

// MODIFY COLUMN id binary(16) ->
// Before (BIGINT) -> 167
// After (BINARY(16)) -> "167\0\0\0\0\0 ...... " -> Not a valid UUID

/*

Product: "Essence mascara" : category_id = 1 -> "beauty"
Product: "product 2" : category_id = 1 -> "beauty"
1 -> randomUUIDv7

1. current: category_id (BIG_INT)
   create a new row: new_category_id ()


 */