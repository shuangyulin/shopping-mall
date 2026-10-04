package com.mall.domain;

import com.mall.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_no", columnList = "orderNo", unique = true),
        @Index(name = "idx_order_user", columnList = "user_id")
})
public class MallOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(nullable = false, length = 50)
    private String receiverName;

    @Column(nullable = false, length = 30)
    private String receiverPhone;

    @Column(nullable = false, length = 300)
    private String receiverAddress;

    @Column(length = 300)
    private String remark;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    private LocalDateTime shippedAt;

    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = OrderStatus.PENDING_PAYMENT;
        }
    }
}
