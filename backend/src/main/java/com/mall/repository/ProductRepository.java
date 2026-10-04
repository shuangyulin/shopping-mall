package com.mall.repository;

import com.mall.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
            select p from Product p left join p.category c
            where p.status = true
              and (:keyword is null or lower(p.name) like lower(concat('%', :keyword, '%'))
                   or lower(p.subtitle) like lower(concat('%', :keyword, '%')))
              and (:categoryId is null or c.id = :categoryId)
            """)
    Page<Product> search(@Param("keyword") String keyword,
                         @Param("categoryId") Long categoryId,
                         Pageable pageable);

    @Query("""
            select p from Product p left join p.category c
            where (:keyword is null or lower(p.name) like lower(concat('%', :keyword, '%')))
              and (:categoryId is null or c.id = :categoryId)
            """)
    Page<Product> adminSearch(@Param("keyword") String keyword,
                              @Param("categoryId") Long categoryId,
                              Pageable pageable);

    long countByStatusTrue();

    long countByCategoryId(Long categoryId);
}
