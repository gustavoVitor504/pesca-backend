package com.example.pesca.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void enviarVerificacaoEmail(String toEmail, String token) {
        String link = frontendUrl + "/verificar-email?token=" + token;
        String html = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #1a6478;">Bem-vindo à Tuvira & Tevaro!</h2>
                <p>Clique no botão abaixo para verificar seu email e ativar sua conta:</p>
                <a href="%s" style="display:inline-block; background:#1a6478; color:white;
                   padding:12px 24px; border-radius:8px; text-decoration:none; margin:16px 0;">
                   Verificar Email
                </a>
                <p style="color:#666; font-size:0.9rem;">
                    Se você não criou uma conta, ignore este email.<br>
                    Link válido por 24 horas.
                </p>
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
                <p>Você receberá atualizações sobre seu pedido por email.</p>
                <p style="color:#666;">Obrigado por comprar na Tuvira & Tevaro!</p>
            </div>
            """.formatted(nomeCliente, pedidoId, total);

        enviarEmail(toEmail, "Pedido #" + pedidoId + " recebido - Tuvira & Tevaro", html);
    }

    private void enviarEmail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email para " + to + ": " + e.getMessage());
        }
    }
}