package com.example.pesca.controller;

import com.example.pesca.repository.OrderRepository;
import com.example.pesca.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    public WebhookController(OrderRepository orderRepository, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }

    @PostMapping("/mp")
    public ResponseEntity<Void> handleWebhook(
            @RequestParam(required = false) String type,
            @RequestBody Map<String, Object> body) {

        if ("payment".equals(type)) {
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            if (data != null) {
                String paymentId = data.get("id").toString();
                paymentService.atualizarStatusPorPaymentId(paymentId);
            }
        }
        return ResponseEntity.ok().build();
    }
}