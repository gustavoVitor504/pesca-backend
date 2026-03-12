package com.example.pesca.service;

import com.example.pesca.dto.AdminDTO;
import com.example.pesca.model.Order;
import com.example.pesca.model.Product;
import com.example.pesca.repository.OrderRepository;
import com.example.pesca.repository.ProductRepository;
import com.example.pesca.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    // ===== PRODUTOS =====
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product createProduct(AdminDTO.ProductRequest request) {
        Product product = new Product();
        mapRequestToProduct(request, product);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, AdminDTO.ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        mapRequestToProduct(request, product);
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private void mapRequestToProduct(AdminDTO.ProductRequest request, Product product) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setCategory(request.getCategory());
        product.setImage(request.getImage());
        product.setDiscount(request.getDiscount());
        product.setRating(request.getRating() != null ? request.getRating() : 5);
        product.setStock(request.getStock() != null ? request.getStock() : 0);
    }

    // ===== PEDIDOS =====
    @Transactional(readOnly = true)
    public List<AdminDTO.OrderSummary> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toOrderSummary)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdminDTO.OrderSummary updateOrderStatus(Long id, Order.Status status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        order.setStatus(status);
        return toOrderSummary(orderRepository.save(order));
    }

    private AdminDTO.OrderSummary toOrderSummary(Order order) {
        AdminDTO.OrderSummary summary = new AdminDTO.OrderSummary();

        summary.setId(order.getId());
        summary.setUserName(order.getUser().getName());
        summary.setUserEmail(order.getUser().getEmail());
        summary.setTotal(order.getTotal());
        summary.setStatus(order.getStatus());
        summary.setCreatedAt(order.getCreatedAt());
        summary.setItems(order.getItems().stream().filter(item -> item != null && item.getProduct() != null).map(item -> {
            AdminDTO.OrderSummary.ItemSummary i = new AdminDTO.OrderSummary.ItemSummary();
            i.setProductName(item.getProduct().getName());
            i.setQuantity(item.getQuantity());
            i.setPrice(item.getUnitPrice());
            return i;
        }).collect(Collectors.toList()));
        return summary;
    }

    // ===== DASHBOARD =====
    public AdminDTO.DashboardStats getDashboardStats() {
        AdminDTO.DashboardStats stats = new AdminDTO.DashboardStats();
        stats.setTotalOrders(orderRepository.count());
        stats.setPendingOrders(orderRepository.countByStatus(Order.Status.PENDING));
        stats.setConfirmedOrders(orderRepository.countByStatus(Order.Status.CONFIRMED));
        stats.setTotalRevenue(orderRepository.sumRevenueConfirmed());
        stats.setTotalProducts(productRepository.count());
        stats.setTotalUsers(userRepository.count());
        return stats;
    }
}