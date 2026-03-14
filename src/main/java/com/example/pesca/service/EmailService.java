package com.example.pesca.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmailService {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void enviarVerificacaoEmail(String toEmail, String token) {
        String link = frontendUrl + "/verificar-email?token=" + token;
        String html = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #1a6478;">Bem-vindo à Tuvira & Tevaro!</h2>
                <p>Clique no botão abaixo para verificar seu email:</p>
                <a href="%s" style="display:inline-block; background:#1a6478; color:white;
                   padding:12px 24px; border-radius:8px; text-decoration:none; margin:16px 0;">
                   Verificar Email
                </a>
                <p style="color:#666; font-size:0.9rem;">Link válido por 24 horas.</p>
            </div>
            """.formatted(link);

        enviarEmail(toEmail, "Verifique seu email - Tuvira & Tevaro", html);
    }

    public void enviarConfirmacaoPedido(String toEmail, String nomeCliente,
                                        Long pedidoId, BigDecimal total) {
        String html = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #1a6478;">Pedido Confirmado! 🎣</h2>
                <p>Olá, <strong>%s</strong>!</p>
                <p>Seu pedido foi recebido com sucesso.</p>
                <div style="background:#f5f5f5; padding:16px; border-radius:8px; margin:16px 0;">
                    <p><strong>Pedido:</strong> #%d</p>
                    <p><strong>Total:</strong> R$ %.2f</p>
                    <p><strong>Status:</strong> Pendente</p>
                </div>
                <p style="color:#666;">Obrigado por comprar na Tuvira & Tevaro!</p>
            </div>
            """.formatted(nomeCliente, pedidoId, total);

        enviarEmail(toEmail, "Pedido #" + pedidoId + " recebido - Tuvira & Tevaro", html);
    }

    private void enviarEmail(String to, String subject, String html) {
        try {
            Email from = new Email(fromEmail);
            Email toEmail = new Email(to);
            Content content = new Content("text/html", html);
            Mail mail = new Mail(from, subject, toEmail, content);

            SendGrid sg = new SendGrid(apiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println("Email enviado. Status: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
        }
    }
}