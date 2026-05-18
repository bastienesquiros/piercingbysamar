package com.besquiros.piercingbysamar.service;

import com.besquiros.piercingbysamar.entity.Order;
import com.besquiros.piercingbysamar.entity.OrderItem;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
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

    // ── Emails publics ─────────────────────────────────────────

    public void sendOrderConfirmation(Order order) {
        String subject = "Confirmation de commande " + order.getReference() + " — " + shopName;
        String body = buildOrderConfirmationHtml(order);
        send(order.getCustomerEmail(), subject, body);
    }

    public void sendPaymentConfirmation(Order order) {
        String subject = "Paiement reçu — " + order.getReference() + " — " + shopName;
        String body = buildPaymentConfirmationHtml(order);
        send(order.getCustomerEmail(), subject, body);
    }

    public void sendShippingNotification(Order order) {
        String subject = "Votre commande " + order.getReference() + " est expédiée ! — " + shopName;
        String body = buildShippingNotificationHtml(order);
        send(order.getCustomerEmail(), subject, body);
    }

    public void sendClickCollectReady(Order order) {
        String subject = "Votre commande " + order.getReference() + " est prête à retirer ! — " + shopName;
        String body = buildClickCollectReadyHtml(order);
        send(order.getCustomerEmail(), subject, body);
    }

    public void sendCancellation(Order order) {
        String subject = "Commande " + order.getReference() + " annulée — " + shopName;
        String body = buildCancellationHtml(order);
        send(order.getCustomerEmail(), subject, body);
    }

    public void sendCollected(Order order) {
        String subject = "Merci pour votre visite ! — " + shopName;
        String body = buildCollectedHtml(order);
        send(order.getCustomerEmail(), subject, body);
    }

    public void sendAdminNewOrder(Order order) {
        String subject = "🛍️ Nouvelle commande " + order.getReference() + " — " + order.getCustomerName();
        String body = buildAdminNewOrderHtml(order);
        send(adminEmail, subject, body);
    }

    public void sendReviewRequest(Order order) {
        String subject = "Votre avis compte ! ⭐ — " + shopName;
        String body = buildReviewRequestHtml(order);
        send(order.getCustomerEmail(), subject, body);
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
        } catch (MessagingException e) {
            log.error("Erreur lors de l'envoi de l'email à {} : {}", to, e.getMessage());
        }
    }

    // ── Templates HTML ─────────────────────────────────────────

    private String buildOrderConfirmationHtml(Order order) {
        String typeLabel = order.getOrderType().name().equals("CLICK_COLLECT")
                ? "Click & Collect"
                : "Livraison à domicile";

        String addressBlock = order.getShippingAddress() != null ? """
                <p style="margin:4px 0">📍 %s, %s %s, %s</p>
                """.formatted(
                order.getShippingAddress(),
                order.getShippingPostalCode(),
                order.getShippingCity(),
                order.getShippingCountry()) : "";

        String clickCollectNote = order.getOrderType().name().equals("CLICK_COLLECT") ? """
                <div style="background:#FAF7F4;border-left:4px solid #C4A882;padding:16px;margin:16px 0;border-radius:4px">
                  <p style="margin:0;font-weight:600">Click &amp; Collect — Marrakech</p>
                  <p style="margin:8px 0 0;color:#666">Paiement sur place : espèces ou virement.</p>
                  <p style="margin:8px 0 0">
                    <a href="https://maps.app.goo.gl/b4qDcoBaafQaAqzMA"
                       style="color:#C4A882;font-size:13px">
                      📍 Voir la boutique sur Google Maps
                    </a>
                  </p>
                </div>
                """ : """
                <div style="background:#FAF7F4;border-left:4px solid #C4A882;padding:16px;margin:16px 0;border-radius:4px">
                  <p style="margin:0;font-weight:600">Paiement en cours de traitement</p>
                  <p style="margin:8px 0 0;color:#666">Vous recevrez un second email dès que votre paiement Stripe sera confirmé et votre commande mise en préparation.</p>
                </div>
                """;

        return wrapLayout(order, """
                <h2 style="color:#2C1810;margin-top:0">Merci pour votre commande, %s !</h2>
                <p>Nous avons bien reçu votre commande <strong>%s</strong>.</p>
                <p>Mode : <strong>%s</strong></p>
                %s
                %s
                %s
                """.formatted(
                order.getCustomerName(),
                order.getReference(),
                typeLabel,
                addressBlock,
                buildItemsTable(order),
                clickCollectNote));
    }

    private String buildPaymentConfirmationHtml(Order order) {
        return wrapLayout(order, """
                <h2 style="color:#2C1810;margin-top:0">Paiement confirmé ✓</h2>
                <p>Bonjour %s,</p>
                <p>Votre paiement pour la commande <strong>%s</strong> a bien été reçu.</p>
                %s
                <p style="margin-top:24px;color:#666">Votre commande est en cours de préparation. Vous recevrez un email dès qu'elle sera expédiée.</p>
                """.formatted(
                order.getCustomerName(),
                order.getReference(),
                buildItemsTable(order)));
    }

    private String buildShippingNotificationHtml(Order order) {
        return wrapLayout(order, """
                <h2 style="color:#2C1810;margin-top:0">Votre commande est en route ! 🚚</h2>
                <p>Bonjour %s,</p>
                <p>Votre commande <strong>%s</strong> vient d'être expédiée.</p>
                <p>Adresse de livraison :<br>
                   <strong>%s, %s %s, %s</strong></p>
                %s
                <p style="margin-top:24px;color:#666">Si vous avez des questions, répondez simplement à cet email.</p>
                """.formatted(
                order.getCustomerName(),
                order.getReference(),
                order.getShippingAddress(),
                order.getShippingPostalCode(),
                order.getShippingCity(),
                order.getShippingCountry(),
                buildItemsTable(order)));
    }

    private String buildClickCollectReadyHtml(Order order) {
        return wrapLayout(order, """
                <h2 style="color:#2C1810;margin-top:0">Votre commande est prête ! 🎉</h2>
                <p>Bonjour %s,</p>
                <p>Votre commande <strong>%s</strong> est prête à être retirée en boutique.</p>
                <div style="background:#FAF7F4;border-left:4px solid #C4A882;padding:16px;margin:16px 0;border-radius:4px">
                  <p style="margin:0;font-weight:600">Informations de retrait</p>
                  <p style="margin:8px 0 0">Pensez à vous munir de votre numéro de commande : <strong>%s</strong></p>
                  <p style="margin:4px 0 0;color:#666">Paiement accepté : espèces et virement</p>
                  <p style="margin:8px 0 0">
                    <a href="https://maps.app.goo.gl/b4qDcoBaafQaAqzMA"
                       style="color:#C4A882;font-size:13px">
                      📍 Voir la boutique sur Google Maps
                    </a>
                  </p>
                </div>
                %s
                """.formatted(
                order.getCustomerName(),
                order.getReference(),
                order.getReference(),
                buildItemsTable(order)));
    }

    private String buildCancellationHtml(Order order) {
        String waUrl = "https://wa.me/" + whatsappNumber + "?text=" +
                java.net.URLEncoder.encode(
                        "Bonjour, j'ai une question concernant ma commande " + order.getReference(),
                        java.nio.charset.StandardCharsets.UTF_8);
        return wrapLayout(order, """
                <h2 style="color:#2C1810;margin-top:0">Commande annulée</h2>
                <p>Bonjour %s,</p>
                <p>Votre commande <strong>%s</strong> a été annulée.</p>
                <p style="color:#666">Si vous pensez qu'il s'agit d'une erreur ou si vous avez des questions, contactez-nous directement sur WhatsApp — c'est le moyen le plus rapide pour nous joindre.</p>
                <div style="text-align:center;margin:24px 0">
                  <a href="%s"
                     style="display:inline-block;background:#25D366;color:#fff;text-decoration:none;padding:12px 28px;border-radius:8px;font-weight:600;font-size:15px">
                    💬 Nous contacter sur WhatsApp
                  </a>
                  <p style="margin:12px 0 0;color:#999;font-size:13px">+212 7 81 57 00 83</p>
                </div>
                %s
                """.formatted(
                order.getCustomerName(),
                order.getReference(),
                waUrl,
                buildItemsTable(order)));
    }

    private String buildCollectedHtml(Order order) {
        return wrapLayout(order, """
                <h2 style="color:#2C1810;margin-top:0">Merci pour votre visite ! ✨</h2>
                <p>Bonjour %s,</p>
                <p>Votre commande <strong>%s</strong> a bien été retirée en boutique. Merci de votre confiance !</p>
                <p style="color:#666">Nous espérons vous revoir bientôt chez Piercing by Samar.</p>
                %s
                """.formatted(
                order.getCustomerName(),
                order.getReference(),
                buildItemsTable(order)));
    }

    private String buildAdminNewOrderHtml(Order order) {
        String typeLabel = order.getOrderType().name().equals("CLICK_COLLECT") ? "Click & Collect" : "Livraison";
        String waUrl = "https://wa.me/" + order.getCustomerPhone();
        return wrapLayout(order, """
                <h2 style="color:#2C1810;margin-top:0">🛍️ Nouvelle commande reçue</h2>
                <div style="background:#FAF7F4;border-left:4px solid #C4A882;padding:16px;margin:16px 0;border-radius:4px">
                  <p style="margin:0;font-weight:600">%s — %s</p>
                  <p style="margin:6px 0 0;color:#666">%s · %s</p>
                  %s
                </div>
                %s
                """.formatted(
                order.getReference(),
                typeLabel,
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getCustomerPhone() != null
                    ? "<p style=\"margin:6px 0 0\"><a href=\"" + waUrl + "\" style=\"color:#25D366;font-weight:600\">💬 " + order.getCustomerPhone() + "</a></p>"
                    : "",
                buildItemsTable(order)));
    }

    private String buildReviewRequestHtml(Order order) {
        return wrapLayout(order, """
                <h2 style="color:#2C1810;margin-top:0">Votre avis nous aide beaucoup ⭐</h2>
                <p>Bonjour %s,</p>
                <p>Nous espérons que vous êtes satisfait(e) de votre commande <strong>%s</strong> chez Piercing by Samar.</p>
                <p style="color:#666">Si vous avez un moment, laisser un avis Google nous aide énormément à faire connaître la boutique.</p>
                <div style="text-align:center;margin:28px 0">
                  <a href="%s"
                     style="display:inline-block;background:#C4A882;color:#fff;text-decoration:none;padding:14px 32px;border-radius:8px;font-weight:600;font-size:15px">
                    ⭐ Laisser un avis Google
                  </a>
                </div>
                <p style="text-align:center;margin:0">
                  <a href="https://www.instagram.com/piercing_bysamar" style="color:#C4A882;font-size:13px">Nous suivre sur Instagram</a>
                </p>
                <p style="margin-top:24px;color:#999;font-size:12px;text-align:center">
                  Si vous avez la moindre question, on est là sur
                  <a href="https://wa.me/%s" style="color:#25D366">WhatsApp</a>.
                </p>
                """.formatted(
                order.getCustomerName(),
                order.getReference(),
                googleReviewUrl,
                whatsappNumber));
    }

    private String buildItemsTable(Order order) {
        boolean isClickCollect = "CLICK_COLLECT".equals(order.getOrderType().name());
        StringBuilder rows = new StringBuilder();
        for (OrderItem item : order.getItems()) {
            String label = item.getSnapshotVariantLabel() != null && !item.getSnapshotVariantLabel().isBlank()
                    ? " — " + item.getSnapshotVariantLabel()
                    : "";
            String priceStr = formatAmount(item.getTotalCents(), order.getCurrency());
            rows.append("""
                    <tr>
                      <td style="padding:8px 0;border-bottom:1px solid #f0ece8">%s%s × %d</td>
                      <td style="padding:8px 0;border-bottom:1px solid #f0ece8;text-align:right">%s</td>
                    </tr>
                    """.formatted(
                    item.getSnapshotProductName(), label, item.getQuantity(), priceStr));
        }

        String totalStr = formatAmount(order.getTotalCents(), order.getCurrency());
        String totalLabel = isClickCollect ? "À payer en boutique" : "Total payé";

        return """
                <table style="width:100%%;border-collapse:collapse;margin:16px 0">
                  <thead>
                    <tr style="color:#C4A882;font-size:12px;text-transform:uppercase">
                      <th style="text-align:left;padding-bottom:8px">Article</th>
                      <th style="text-align:right;padding-bottom:8px">Prix</th>
                    </tr>
                  </thead>
                  <tbody>%s</tbody>
                  <tfoot>
                    <tr>
                      <td style="padding-top:12px;font-weight:700">%s</td>
                      <td style="padding-top:12px;font-weight:700;text-align:right">%s</td>
                    </tr>
                  </tfoot>
                </table>
                """.formatted(rows, totalLabel, totalStr);
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

    private String wrapLayout(Order order, String content) {
        return """
                <!DOCTYPE html>
                <html lang="fr">
                <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head>
                <body style="margin:0;padding:0;background:#f5f1ed;font-family:Georgia,serif;color:#2C1810">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f5f1ed;padding:32px 0">
                    <tr><td align="center">
                      <table width="600" cellpadding="0" cellspacing="0" style="max-width:600px;width:100%%">

                        <!-- Header -->
                        <tr>
                          <td style="background:#C4A882;padding:24px 32px;border-radius:8px 8px 0 0;text-align:center">
                            <h1 style="margin:0;color:#fff;font-size:22px;letter-spacing:2px">PIERCING BY SAMAR</h1>
                          </td>
                        </tr>

                        <!-- Body -->
                        <tr>
                          <td style="background:#fff;padding:32px;border-radius:0 0 8px 8px">
                            %s
                          </td>
                        </tr>

                        <!-- Footer -->
                        <tr>
                          <td style="padding:16px 32px;text-align:center;color:#999;font-size:12px">
                            Commande n° %s &bull; Piercing by Samar &bull; <a href="https://piercingbysamar.com" style="color:#C4A882">piercingbysamar.com</a><br>
                            <a href="https://wa.me/%s" style="color:#999;text-decoration:none">💬 +212 7 81 57 00 83</a>
                          </td>
                        </tr>

                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(content, order.getReference(), whatsappNumber);
    }
}
