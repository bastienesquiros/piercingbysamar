package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.entity.ProductVariant;
import com.besquiros.piercingbysamar.repository.ProductVariantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/import")
@RequiredArgsConstructor
public class ImportController {

    private final ProductVariantRepository variantRepository;

    /** Télécharger le template CSV pré-rempli avec tous les SKUs + stock actuel */
    @GetMapping("/stock-template.csv")
    @Transactional
    public ResponseEntity<byte[]> downloadTemplate() {
        List<ProductVariant> variants = variantRepository.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("SKU,Produit,Taille,Couleur,Stock actuel,Nouveau stock\n");
        variants.stream()
                .filter(ProductVariant::isActive)
                .sorted((a, b) -> {
                    String nameA = a.getProduct() != null ? a.getProduct().getName() : "";
                    String nameB = b.getProduct() != null ? b.getProduct().getName() : "";
                    int cmp = nameA.compareTo(nameB);
                    return cmp != 0 ? cmp : a.getSku().compareTo(b.getSku());
                })
                .forEach(v -> {
                    String product = v.getProduct() != null ? v.getProduct().getName() : "";
                    sb.append(escapeCsv(v.getSku())).append(',')
                      .append(escapeCsv(product)).append(',')
                      .append(escapeCsv(v.getSize())).append(',')
                      .append(escapeCsv(v.getColor())).append(',')
                      .append(v.getStock()).append(',')
                      .append('\n'); // Nouveau stock vide = pas de changement
                });

        byte[] csv = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"template_stocks.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }

    /** Importer un CSV et mettre à jour les stocks */
    @PostMapping("/stock")
    @Transactional
    public ResponseEntity<ImportResult> importStock(@RequestParam("file") MultipartFile file) {
        // Validation basique du fichier
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
        if (!filename.endsWith(".csv") && !filename.endsWith(".CSV")) {
            return ResponseEntity.badRequest()
                    .body(new ImportResult(0, List.of("Format invalide : seuls les fichiers .csv sont acceptés."), List.of()));
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ImportResult(0, List.of("Le fichier est vide."), List.of()));
        }

        List<String> updated = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                return ResponseEntity.badRequest()
                        .body(new ImportResult(0, List.of("Fichier vide ou entête manquante."), List.of()));
            }
            // Vérification que c'est bien notre template
            if (!headerLine.toLowerCase().contains("sku")) {
                return ResponseEntity.badRequest()
                        .body(new ImportResult(0, List.of("Entête non reconnue. Utilisez le template téléchargeable (colonnes : SKU, Produit, Taille, Couleur, Stock actuel, Nouveau stock)."), List.of()));
            }

            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (line.trim().isEmpty()) continue;

                String[] cols = parseCsvLine(line);
                if (cols.length < 6) {
                    errors.add("Ligne " + lineNum + " : trop peu de colonnes (attendu 6, trouvé " + cols.length + "). Vérifiez que vous utilisez bien le template.");
                    continue;
                }

                String sku = cols[0].trim();
                String newStockStr = cols[5].trim();

                if (sku.isEmpty()) continue;
                if (newStockStr.isEmpty()) continue; // pas de changement

                int newStock;
                try {
                    newStock = Integer.parseInt(newStockStr);
                    if (newStock < 0) {
                        errors.add("Ligne " + lineNum + " (" + sku + ") : le stock ne peut pas être négatif (valeur : " + newStockStr + ").");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    errors.add("Ligne " + lineNum + " (" + sku + ") : « " + newStockStr + " » n'est pas un nombre entier valide.");
                    continue;
                }

                final int stockToSet = newStock;
                final String finalSku = sku;
                final int finalLine = lineNum;
                variantRepository.findBySku(sku).ifPresentOrElse(
                        v -> {
                            v.setStock(stockToSet);
                            variantRepository.save(v);
                            updated.add(finalSku + " → " + stockToSet);
                        },
                        () -> errors.add("Ligne " + finalLine + " : SKU « " + finalSku + " » introuvable dans la base.")
                );
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ImportResult(0, List.of("Erreur lors de la lecture du fichier : " + e.getMessage()), List.of()));
        }

        return ResponseEntity.ok(new ImportResult(updated.size(), errors, updated));
    }

    /** Parse une ligne CSV simple (gère les guillemets) */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n"))
            return "\"" + value.replace("\"", "\"\"") + "\"";
        return value;
    }

    public record ImportResult(int updatedCount, List<String> errors, List<String> updated) {}
}
