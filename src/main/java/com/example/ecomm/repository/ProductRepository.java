package com.example.ecomm.repository;

import com.example.ecomm.models.Product;
import com.example.ecomm.models.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // select * from table where id = x and state = y
    Optional<Product> findByIdAndState(Long id, State state);

//    List<Product> findAllByState(State state);

    // Query-returning Page must have a Pageable parameter.
    @EntityGraph(attributePaths = "category")
    @Query("SELECT p FROM Product p WHERE p.state = :state " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findAllByPriceBetweenAndState(@Param("minPrice") BigDecimal minPrice,
                                               @Param("maxPrice") BigDecimal maxPrice,
                                               @Param("state") State state, Pageable pageable);

}
