package com.example.pesca.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal originalPrice;

    @Column(nullable = false)
    private String category;

    private String image;

    @Column(columnDefinition = "text")
    private String images;

    private Integer discount;

    @Builder.Default
    private Integer rating = 5;

    @Builder.Default
    private Integer stock = 0;
}