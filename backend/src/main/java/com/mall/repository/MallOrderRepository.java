package com.mall.repository;

import com.mall.domain.MallOrder;
import com.mall.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface MallOrderRepository extends JpaRepository<MallOrder, Long> {
    Page<MallOrder> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<MallOrder> findByOrderNoAndUserId(String orderNo, Long userId);

    Optional<MallOrder> findByOrderNo(String orderNo);

    long countByStatus(OrderStatus status);

    @Query("select coalesce(sum(o.totalAmount), 0) from MallOrder o where o.status <> com.mall.domain.enums.OrderStatus.CANCELLED")
    BigDecimal sumValidAmount();

    @Query("""
            select o from MallOrder o
            where (:status is null or o.status = :status)
              and (:keyword is null or lower(o.orderNo) like lower(concat('%', :keyword, '%')))
            order by o.createdAt desc
            """)
    Page<MallOrder> adminSearch(@Param("keyword") String keyword,
                                @Param("status") OrderStatus status,
                                Pageable pageable);
}
