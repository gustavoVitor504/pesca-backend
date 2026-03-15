package com.example.pesca.dto;

import com.example.pesca.model.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AdminDTO {

    @Data
    public static class ProductRequest {
        private String name;
        private String description;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private String category;
        private String image;
        private String images;
        private Integer discount;
        private Integer rating;
        private Integer stock;
    }

    @Data
    public static class StatusRequest {
        private Order.Status status;
    }

    @Data
    public static class OrderSummary {
        private Long id;
        private String userName;
        private String userEmail;
        private BigDecimal total;
        private Order.Status status;
        private LocalDateTime createdAt;
        private List<ItemSummary> items;

        @Data
        public static class ItemSummary {
            private String productName;
            private Integer quantity;
            private BigDecimal price;
        }
    }

    @Data
    public static class DashboardStats {
        private Long totalOrders;
        private Long pendingOrders;
        private Long confirmedOrders;
        private BigDecimal totalRevenue;
        private Long totalProducts;
        private Long totalUsers;
    }
}