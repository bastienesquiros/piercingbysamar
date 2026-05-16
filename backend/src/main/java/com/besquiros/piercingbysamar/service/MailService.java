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

    @Value("${app.mail.from:noreply@piercingbysamar.com}")
    private String from;

    @Value("${app.mail.shop-name:Piercing by Samar}")
    private String shopName;

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

        return wrapLayout(order, """
                <h2 style="color:#2C1810;margin-top:0">Merci pour votre commande, %s !</h2>
                <p>Nous avons bien reçu votre commande <strong>%s</strong>.</p>
                <p>Mode : <strong>%s</strong></p>
                %s
                %s
                <p style="margin-top:24px;color:#666">Vous recevrez un email de confirmation dès que votre paiement sera validé.</p>
                """.formatted(
                order.getCustomerName(),
                order.getReference(),
                typeLabel,
                addressBlock,
                buildItemsTable(order)));
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
                  <p style="margin:4px 0 0;color:#666">Paiement accepté : espèces et carte bancaire</p>
                </div>
                %s
                """.formatted(
                order.getCustomerName(),
                order.getReference(),
                order.getReference(),
                buildItemsTable(order)));
    }

    private String buildItemsTable(Order order) {
        StringBuilder rows = new StringBuilder();
        for (OrderItem item : order.getItems()) {
            String label = item.getSnapshotVariantLabel() != null && !item.getSnapshotVariantLabel().isBlank()
                    ? " — " + item.getSnapshotVariantLabel()
                    : "";
            rows.append("""
                    <tr>
                      <td style="padding:8px 0;border-bottom:1px solid #f0ece8">%s%s × %d</td>
                      <td style="padding:8px 0;border-bottom:1px solid #f0ece8;text-align:right">%.2f €</td>
                    </tr>
                    """.formatted(
                    item.getSnapshotProductName(), label, item.getQuantity(),
                    item.getTotalCents() / 100.0));
        }

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
                      <td style="padding-top:12px;font-weight:700">Total</td>
                      <td style="padding-top:12px;font-weight:700;text-align:right">%.2f €</td>
                    </tr>
                  </tfoot>
                </table>
                """.formatted(rows, order.getTotalCents() / 100.0);
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
                            Commande n° %s &bull; Piercing by Samar &bull; <a href="https://piercingbysamar.com" style="color:#C4A882">piercingbysamar.com</a>
                          </td>
                        </tr>

                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(content, order.getReference());
    }
}
