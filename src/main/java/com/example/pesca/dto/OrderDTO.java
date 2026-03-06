package com.example.pesca.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    @Data
    public static class ItemRequest {
        @NotNull
        private Long productId;

        @Min(1)
        private Integer quantity;
    }

    @Data
    public static class CreateRequest {
        @NotEmpty
        private List<ItemRequest> items;
    }

    @Data
    public static class ItemResponse {
        private Long productId;
        private String productName;
        private String productImage;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }

    @Data
    public static class OrderResponse {
        private Long id;
        private List<ItemResponse> items;
        private BigDecimal total;
        private String status;
        private LocalDateTime createdAt;
    }
}