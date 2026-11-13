package com.example.ecomm.repository;

import com.example.ecomm.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);

    @Query("""
            SELECT c.name AS name, COUNT(p.id) AS productCount
            FROM Category c LEFT JOIN Product p ON p.category = c
            GROUP BY c.id, c.name
            ORDER BY c.name
            """)
    List<CategoryProductCount> findProductCounts();

    interface CategoryProductCount {
        String getName();

        long getProductCount();
    }
}
