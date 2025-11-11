package com.mhyusuf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "variants")
public class Variant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "variant_name", nullable = false, length = 100)
    private String variantName;

    @Column(length = 50)
    private String color;

    @Column(length = 20)
    private String size;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt = new Date();

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date updatedAt = new Date();

    public Variant(Item item, String variantName, String color, String size, BigDecimal price, Integer stock) {
        this.item = item;
        this.variantName = variantName;
        this.color = color;
        this.size = size;
        this.price = price;
        this.stock = stock;
    }
}

