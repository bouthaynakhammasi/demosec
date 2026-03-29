package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.repository.PharmacyOrderRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Generates PDF invoices for pharmacy orders using iText 8.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceService {

    private final PharmacyOrderRepository orderRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generateInvoice(Long orderId) {
        PharmacyOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfDocument pdf = new PdfDocument(new PdfWriter(baos));
             Document doc = new Document(pdf)) {

            // ── HEADER ──────────────────────────────────────
            doc.add(new Paragraph("MediCareAI")
                    .setFontSize(22)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY));

            doc.add(new Paragraph("Facture / Invoice")
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));

            doc.add(new Paragraph("\n"));

            // ── ORDER INFO ───────────────────────────────────
            doc.add(new Paragraph("Détails de la Commande").setFontSize(13).setBold());
            doc.add(new Paragraph("Numéro de commande : #" + order.getId()));
            doc.add(new Paragraph("Date : " + order.getCreatedAt().format(DATE_FMT)));
            doc.add(new Paragraph("Statut : " + order.getStatus()));
            doc.add(new Paragraph("Pharmacie : " + (order.getPharmacy() != null ? order.getPharmacy().getName() : "N/A")));
            doc.add(new Paragraph("Patient : " + (order.getPatient() != null ? order.getPatient().getFullName() : "N/A")));
            doc.add(new Paragraph("Adresse de livraison : " + order.getDeliveryAddress()));

            doc.add(new Paragraph("\n"));

            // ── ITEMS TABLE ──────────────────────────────────
            doc.add(new Paragraph("Articles").setFontSize(13).setBold());

            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 20, 20, 20}))
                    .setWidth(UnitValue.createPercentValue(100));

            // Table headers
            addTableHeader(table, "Produit");
            addTableHeader(table, "Quantité");
            addTableHeader(table, "Prix Unitaire");
            addTableHeader(table, "Total");

            BigDecimal subtotal = BigDecimal.ZERO;
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    BigDecimal lineTotal = item.getPrice();
                    subtotal = subtotal.add(lineTotal != null ? lineTotal : BigDecimal.ZERO);
                    table.addCell(item.getProduct() != null ? item.getProduct().getName() : "?");
                    table.addCell(String.valueOf(item.getQuantity()));
                    BigDecimal unitPrice = (lineTotal != null && item.getQuantity() > 0)
                            ? lineTotal.divide(BigDecimal.valueOf(item.getQuantity()), 3, java.math.RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    table.addCell(unitPrice + " TND");
                    table.addCell((lineTotal != null ? lineTotal : BigDecimal.ZERO) + " TND");
                }
            }
            doc.add(table);

            doc.add(new Paragraph("\n"));

            // ── TOTAL ────────────────────────────────────────
            doc.add(new Paragraph("TOTAL : " + order.getTotalPrice() + " TND")
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT));

            // ── FOOTER ───────────────────────────────────────
            doc.add(new Paragraph("\n\n"));
            doc.add(new Paragraph("Merci pour votre confiance — PharmaNet Tunisia")
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));
            doc.add(new Paragraph("contact@pharmanet.tn | +216 71 000 000")
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF invoice for order " + orderId, e);
        }

        return baos.toByteArray();
    }

    private void addTableHeader(Table table, String text) {
        table.addHeaderCell(new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
    }
}
