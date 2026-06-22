package com.besquiros.piercingbysamar.service;

import com.besquiros.piercingbysamar.entity.Order;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final ExchangeRateService exchangeRateService;

    @Value("${app.mail.from:noreply@piercingbysamar.com}")
    private String from;

    @Value("${app.mail.shop-name:Piercing by Samar}")
    private String shopName;

    @Value("${app.mail.whatsapp:212781570083}")
    private String whatsappNumber;

    @Value("${app.mail.admin-email:samar@piercingbysamar.com}")
    private String adminEmail;

    @Value("${app.mail.google-review-url:https://maps.app.goo.gl/b4qDcoBaafQaAqzMA}")
    private String googleReviewUrl;

    // ── DTO interne ────────────────────────────────────────────

    public record MailItem(String name, String label, int quantity, String price, String imageUrl) {}

    // ── Emails publics ─────────────────────────────────────────

    public void sendOrderConfirmation(Order order) {
        boolean isClickCollect = isClickCollect(order);
        Context ctx = buildBaseContext(order);
        ctx.setVariable("typeLabel", isClickCollect ? "Click & Collect" : "Livraison à domicile");
        ctx.setVariable("shippingAddress", order.getShippingAddress());
        ctx.setVariable("shippingPostalCode", order.getShippingPostalCode());
        ctx.setVariable("shippingCity", order.getShippingCity());
        ctx.setVariable("shippingCountry", order.getShippingCountry());
        send(order.getCustomerEmail(),
                "Confirmation de commande " + order.getReference() + " — " + shopName,
                render("mail/order-confirmation", ctx));
    }

    public void sendPaymentConfirmation(Order order) {
        send(order.getCustomerEmail(),
                "Paiement reçu — " + order.getReference() + " — " + shopName,
                render("mail/payment-confirmation", buildBaseContext(order)));
    }

    public void sendShippingNotification(Order order) {
        Context ctx = buildBaseContext(order);
        ctx.setVariable("shippingAddress", order.getShippingAddress());
        ctx.setVariable("shippingPostalCode", order.getShippingPostalCode());
        ctx.setVariable("shippingCity", order.getShippingCity());
        ctx.setVariable("shippingCountry", order.getShippingCountry());
        send(order.getCustomerEmail(),
                "Votre commande " + order.getReference() + " est expédiée ! — " + shopName,
                render("mail/shipping-notification", ctx));
    }

    public void sendClickCollectReady(Order order) {
        send(order.getCustomerEmail(),
                "Votre commande " + order.getReference() + " est prête à retirer ! — " + shopName,
                render("mail/click-collect-ready", buildBaseContext(order)));
    }

    public void sendCancellation(Order order) {
        Context ctx = buildBaseContext(order);
        ctx.setVariable("waUrl", "https://wa.me/" + whatsappNumber + "?text=" +
                java.net.URLEncoder.encode(
                        "Bonjour, j'ai une question concernant ma commande " + order.getReference(),
                        java.nio.charset.StandardCharsets.UTF_8));
        send(order.getCustomerEmail(),
                "Commande " + order.getReference() + " annulée — " + shopName,
                render("mail/cancellation", ctx));
    }

    public void sendCollected(Order order) {
        send(order.getCustomerEmail(),
                "Merci pour votre visite ! — " + shopName,
                render("mail/collected", buildBaseContext(order)));
    }

    public void sendAdminNewOrder(Order order) {
        Context ctx = buildBaseContext(order);
        ctx.setVariable("typeLabel", isClickCollect(order) ? "Click & Collect" : "Livraison");
        ctx.setVariable("customerEmail", order.getCustomerEmail());
        ctx.setVariable("customerPhone", order.getCustomerPhone());
        send(adminEmail,
                "🛍️ Nouvelle commande " + order.getReference() + " — " + order.getCustomerName(),
                render("mail/admin-new-order", ctx));
    }

    public void sendReviewRequest(Order order) {
        Context ctx = buildBaseContext(order);
        ctx.setVariable("googleReviewUrl", googleReviewUrl);
        send(order.getCustomerEmail(),
                "Votre avis compte ! ⭐ — " + shopName,
                render("mail/review-request", ctx));
    }

    // ── Helpers ────────────────────────────────────────────────

    private boolean isClickCollect(Order order) {
        return "CLICK_COLLECT".equals(order.getOrderType().name());
    }

    private Context buildBaseContext(Order order) {
        boolean isClickCollect = isClickCollect(order);
        Context ctx = new Context(Locale.FRENCH);
        ctx.setVariable("shopName", shopName);
        ctx.setVariable("customerName", order.getCustomerName());
        ctx.setVariable("orderReference", order.getReference());
        ctx.setVariable("whatsappNumber", whatsappNumber);
        ctx.setVariable("isClickCollect", isClickCollect);
        ctx.setVariable("items", buildItemList(order));
        ctx.setVariable("total", formatAmount(order.getTotalCents(), order.getCurrency()));
        ctx.setVariable("totalLabel", isClickCollect ? "À payer en boutique" : "Total payé");
        return ctx;
    }

    private List<MailItem> buildItemList(Order order) {
        return order.getItems().stream()
                .map(item -> new MailItem(
                        item.getSnapshotProductName(),
                        item.getSnapshotVariantLabel(),
                        item.getQuantity(),
                        formatAmount(item.getTotalCents(), order.getCurrency()),
                        item.getSnapshotImageUrl()))
                .toList();
    }

    /** Convertit des centimes MAD dans la devise de la commande et formate pour l'affichage. */
    private String formatAmount(int madCents, String currency) {
        double madAmount = madCents / 100.0;
        return switch (currency.toUpperCase()) {
            case "EUR" -> String.format("%.2f €", madAmount * exchangeRateService.getMadToEur());
            case "USD" -> String.format("$ %.2f", madAmount * exchangeRateService.getMadToUsd());
            default    -> ((int) madAmount) + " MAD";
        };
    }

    private String render(String template, Context ctx) {
        return templateEngine.process(template, ctx);
    }

    // ── Envoi ──────────────────────────────────────────────────

    private void send(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Email envoyé à {} — {}", to, subject);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email à {} : {}", to, e.getMessage());
        }
    }
}
