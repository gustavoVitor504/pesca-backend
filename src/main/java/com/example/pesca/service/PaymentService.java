package com.example.pesca.service;

import com.example.pesca.dto.PaymentDTO;
import com.example.pesca.model.Order;
import com.example.pesca.repository.OrderRepository;
import com.example.pesca.repository.ProductRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${mp.access-token}")
    private String accessToken;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    @Transactional
    public PaymentDTO.Response processPayment(PaymentDTO.Request request) {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);

            PaymentCreateRequest.PaymentCreateRequestBuilder builder = PaymentCreateRequest.builder()
                    .transactionAmount(BigDecimal.valueOf(request.transactionAmount()))
                    .description(request.description())
                    .paymentMethodId(request.paymentMethodId())
                    .installments(request.installments())
                    .payer(PaymentPayerRequest.builder()
                            .email(request.payer().email())
                            .firstName(request.payer().firstName())
                            .lastName(request.payer().lastName())
                            .build()
                    );

            // Cartão precisa do token
            if (request.token() != null) {
                builder.token(request.token());
            }

            PaymentClient client = new PaymentClient();
            Payment payment = client.create(builder.build());

            // Atualiza status do pedido
            if (request.orderId() != null) {
                orderRepository.findById(request.orderId()).ifPresent(order -> {
                    if ("approved".equals(payment.getStatus())) {
                        order.setStatus(Order.Status.CONFIRMED);
                        decrementarEstoque(order);
                    } else {
                        order.setStatus(Order.Status.PENDING);
                    }
                    orderRepository.save(order);
                });
            }

            // Monta resposta
            String qrCode = null;
            String qrCodeBase64 = null;
            String boletoUrl = null;

            if (payment.getPointOfInteraction() != null
                    && payment.getPointOfInteraction().getTransactionData() != null) {
                qrCode = payment.getPointOfInteraction().getTransactionData().getQrCode();
                qrCodeBase64 = payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();
            }

            if (payment.getTransactionDetails() != null) {
                boletoUrl = payment.getTransactionDetails().getExternalResourceUrl();
            }

            return new PaymentDTO.Response(
                    payment.getStatus(),
                    payment.getStatusDetail(),
                    payment.getId(),
                    qrCode,
                    qrCodeBase64,
                    boletoUrl
            );

        } catch (MPApiException e) {
            // Mostra o erro detalhado da API do Mercado Pago
            System.err.println("MP API Error: " + e.getApiResponse().getContent());
            throw new RuntimeException("Erro MP: " + e.getApiResponse().getContent());
        } catch (Exception e) {
            System.err.println("Erro geral: " + e.getMessage());
            throw new RuntimeException("Erro ao processar pagamento: " + e.getMessage());
        }
    }
    @Transactional
    public void atualizarStatusPorPaymentId(String paymentId) {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.parseLong(paymentId));

            // Busca o pedido pelo external reference ou pelo id do pagamento
            // Por ora atualiza o pedido mais recente com status pending
            if ("approved".equals(payment.getStatus())) {
                orderRepository.findTopByStatusOrderByIdDesc(Order.Status.PENDING)
                        .ifPresent(order -> {
                            order.setStatus(Order.Status.CONFIRMED);
                            decrementarEstoque(order);
                            orderRepository.save(order);
                        });
            }
        } catch (Exception e) {
            System.err.println("Erro no webhook: " + e.getMessage());
        }
    }
    private void decrementarEstoque(Order order) {
        order.getItems().forEach(item -> {
            productRepository.findById(item.getProduct().getId()).ifPresent(product -> {
                int novoEstoque = Math.max(0, product.getStock() - item.getQuantity());
                product.setStock(novoEstoque);
                productRepository.save(product);
            });
        });
    }
}