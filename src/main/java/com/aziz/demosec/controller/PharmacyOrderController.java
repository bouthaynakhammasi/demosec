package com.aziz.demosec.controller;

import com.aziz.demosec.dto.pharmacy.*;
import com.aziz.demosec.service.InvoiceService;
import com.aziz.demosec.service.PharmacyOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy/orders")
@RequiredArgsConstructor
public class PharmacyOrderController {

    private final PharmacyOrderService orderService;
    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<PharmacyOrderResponseDTO> create(
            @Valid @RequestBody PharmacyOrderRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(dto));
    }

    @GetMapping
    public ResponseEntity<List<PharmacyOrderResponseDTO>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PharmacyOrderResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PharmacyOrderResponseDTO>> getByPatient(@PathVariable("patientId") Long patientId) {
        return ResponseEntity.ok(orderService.getByPatient(patientId));
    }

    @GetMapping("/pharmacy/{pharmacyId}")
    public ResponseEntity<List<PharmacyOrderResponseDTO>> getByPharmacy(@PathVariable("pharmacyId") Long pharmacyId) {
        System.out.println("DEBUG: Fetching orders for pharmacy ID: " + pharmacyId);
        return ResponseEntity.ok(orderService.getByPharmacy(pharmacyId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PharmacyOrderResponseDTO> updateStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateOrderStatusRequestDTO dto) {
        return ResponseEntity.ok(orderService.updateStatus(id, dto));
    }

    @GetMapping("/{id}/tracking")
    public ResponseEntity<List<OrderTrackingResponseDTO>> getTracking(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.getTracking(id));
    }

    @GetMapping("/stock/search")
    public ResponseEntity<List<PharmacyStockResponseDTO>> findPharmaciesWithProduct(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "minQty", defaultValue = "0") int minQty) {
        return ResponseEntity.ok(orderService.findPharmaciesWithProduct(productId, minQty));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PharmacyOrderResponseDTO> cancelOrder(
            @PathVariable("id") Long id,
            @Valid @RequestBody CancelOrderRequestDTO dto) {
        return ResponseEntity.ok(orderService.cancelOrder(id, dto.getReason()));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<PharmacyOrderResponseDTO> rejectOrder(
            @PathVariable("id") Long id,
            @Valid @RequestBody RejectOrderRequestDTO dto) {
        return ResponseEntity.ok(orderService.rejectOrder(id, dto));
    }

    @GetMapping("/pharmacy/{pharmacyId}/filter")
    public ResponseEntity<List<PharmacyOrderResponseDTO>> getByPharmacyAndStatus(
            @PathVariable("pharmacyId") Long pharmacyId,
            @RequestParam("status") String status) {
        return ResponseEntity.ok(orderService.getByPharmacyAndStatus(pharmacyId, status));
    }

    @GetMapping("/pharmacy/{pharmacyId}/stats")
    public ResponseEntity<PharmacyStatsResponseDTO> getStats(
            @PathVariable("pharmacyId") Long pharmacyId) {
        return ResponseEntity.ok(orderService.getPharmacyStats(pharmacyId));
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable("id") Long id) {
        byte[] pdfBytes = invoiceService.generateInvoice(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=\"invoice-order-" + id + ".pdf\"")
                .body(pdfBytes);
    }
}
