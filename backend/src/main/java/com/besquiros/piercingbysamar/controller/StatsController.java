package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class StatsController {

    private final OrderRepository orderRepository;

    private static final List<String> REVENUE_STATUSES =
            List.of("PAID", "READY", "SHIPPED", "DELIVERED", "COLLECTED");

    // ── Overview KPIs ──────────────────────────────────────────
    @GetMapping("/stats/overview")
    public ResponseEntity<Map<String, Object>> overview(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(LocalTime.MAX);

        List<Object[]> currentRows = orderRepository.aggregateRevenue(REVENUE_STATUSES, start, end);
        List<Object[]> allRows = orderRepository.aggregateRevenue(REVENUE_STATUSES,
                LocalDate.of(2000, 1, 1).atStartOfDay(), end);

        Object[] current = currentRows.isEmpty() ? new Object[]{null, null} : currentRows.get(0);
        Object[] all = allRows.isEmpty() ? new Object[]{null, null} : allRows.get(0);

        long revenue = current[0] != null ? ((Number) current[0]).longValue() : 0L;
        long count = current[1] != null ? ((Number) current[1]).longValue() : 0L;
        long avg = count > 0 ? revenue / count : 0L;

        long allTimeRevenue = all[0] != null ? ((Number) all[0]).longValue() : 0L;
        long allTimeCount = all[1] != null ? ((Number) all[1]).longValue() : 0L;

        List<Object[]> byStatus = orderRepository.countByStatus(start, end);

        return ResponseEntity.ok(Map.of(
                "revenueCents", revenue,
                "orderCount", count,
                "averageOrderCents", avg,
                "allTimeRevenueCents", allTimeRevenue,
                "allTimeOrderCount", allTimeCount,
                "byStatus", byStatus.stream().map(r -> Map.of(
                        "status", r[0],
                        "count", r[1]
                )).toList()
        ));
    }

    // ── Revenue chart data ─────────────────────────────────────
    @GetMapping("/stats/revenue")
    public ResponseEntity<List<Map<String, Object>>> revenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<Object[]> rows = orderRepository.revenueByDay(
                REVENUE_STATUSES,
                from.atStartOfDay(),
                to.atTime(LocalTime.MAX));

        List<Map<String, Object>> result = rows.stream().map(r -> Map.<String, Object>of(
                "date", r[0].toString(),
                "revenueCents", r[1] != null ? ((Number) r[1]).longValue() : 0L,
                "orderCount", r[2] != null ? ((Number) r[2]).longValue() : 0L
        )).toList();

        return ResponseEntity.ok(result);
    }

    // ── Top products ───────────────────────────────────────────
    @GetMapping("/stats/top-products")
    public ResponseEntity<List<Map<String, Object>>> topProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "10") int limit) {

        List<Object[]> rows = orderRepository.topProducts(
                REVENUE_STATUSES,
                from.atStartOfDay(),
                to.atTime(LocalTime.MAX));

        List<Map<String, Object>> result = rows.stream().limit(limit).map(r -> Map.<String, Object>of(
                "productName", r[0],
                "quantity", r[1] != null ? ((Number) r[1]).longValue() : 0L,
                "revenueCents", r[2] != null ? ((Number) r[2]).longValue() : 0L,
                "productId", r[3] != null ? ((Number) r[3]).longValue() : 0L
        )).toList();

        return ResponseEntity.ok(result);
    }

    // ── Export CSV ─────────────────────────────────────────────
    @GetMapping("/export/orders.csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<Object[]> rows = orderRepository.exportOrders(
                from.atStartOfDay(), to.atTime(LocalTime.MAX));

        StringBuilder sb = new StringBuilder();
        sb.append("Référence,Date,Statut,Type,Client,Email,Téléphone,Total (MAD),Articles\n");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Object[] r : rows) {
            sb.append(escapeCsv(r[0])).append(',')          // reference
              .append(r[1] != null ? ((LocalDateTime) r[1]).format(fmt) : "").append(',') // date
              .append(escapeCsv(r[2])).append(',')          // status
              .append(escapeCsv(r[3])).append(',')          // type
              .append(escapeCsv(r[4])).append(',')          // name
              .append(escapeCsv(r[5])).append(',')          // email
              .append(escapeCsv(r[6])).append(',')          // phone
              .append(r[7] != null ? String.format("%.2f", ((Number) r[7]).doubleValue() / 100.0) : "0.00").append(',')
              .append(escapeCsv(r[8])).append('\n');        // items snapshot
        }

        byte[] csv = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String filename = "commandes_" + from + "_" + to + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }

    private String escapeCsv(Object value) {
        if (value == null) return "";
        String s = value.toString().replace("\"", "\"\"");
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) return "\"" + s + "\"";
        return s;
    }
}
