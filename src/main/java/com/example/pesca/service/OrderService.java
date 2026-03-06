package com.example.pesca.service;

import com.example.pesca.dto.OrderDTO.*;
import com.example.pesca.model.*;
import com.example.pesca.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.pesca.dto.OrderDTO.OrderResponse;
import com.example.pesca.dto.OrderDTO.ItemResponse;
import com.example.pesca.dto.OrderDTO.CreateRequest;
import com.example.pesca.dto.OrderDTO.ItemRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse create(String userEmail, CreateRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Order order = Order.builder().user(user).total(BigDecimal.ZERO).build();

        List<OrderItem> items = request.getItems().stream().map(itemReq -> {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemReq.getProductId()));

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
        }).collect(Collectors.toList());

        order.setItems(items);

        BigDecimal total = items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotal(total);

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    public List<OrderResponse> findByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse res = new OrderResponse();
        res.setId(order.getId());
        res.setTotal(order.getTotal());
        res.setStatus(order.getStatus().name());
        res.setCreatedAt(order.getCreatedAt());

        res.setItems(order.getItems().stream().map(item -> {
            ItemResponse ir = new ItemResponse();
            ir.setProductId(item.getProduct().getId());
            ir.setProductName(item.getProduct().getName());
            ir.setProductImage(item.getProduct().getImage());
            ir.setQuantity(item.getQuantity());
            ir.setUnitPrice(item.getUnitPrice());
            ir.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return ir;
        }).collect(Collectors.toList()));

        return res;
    }
}