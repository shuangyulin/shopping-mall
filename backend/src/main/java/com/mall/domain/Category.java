package com.mall.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 500)
    private String icon;

    @Column(nullable = false)
    private Integer sortOrder;

    @Column(nullable = false)
    private Boolean enabled;

    @PrePersist
    void onCreate() {
        if (sortOrder == null) {
            sortOrder = 0;
        }
        if (enabled == null) {
            enabled = true;
        }
    }
}
