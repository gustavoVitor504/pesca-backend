package com.example.pesca.dto;

public class PaymentDTO {

    public record Request(
            String paymentMethodId,  // "pix", "visa", "master", "bolbradesco"
            String token,            // token do cartão (gerado pelo frontend com SDK MP)
            Integer installments,    // parcelas (1 para PIX/boleto)
            Double transactionAmount,
            String description,
            Payer payer,
            Long orderId
    ) {}

    public record Payer(
            String email,
            String firstName,
            String lastName,
            Identification identification
    ) {}

    public record Identification(
            String type,   // "CPF"
            String number
    ) {}

    public record Response(
            String status,          // "approved", "pending", "rejected"
            String statusDetail,
            Long paymentId,
            String qrCode,          // para PIX
            String qrCodeBase64,    // imagem do QR PIX
            String boletoUrl        // para boleto
    ) {}
}