package com.aziz.demosec.dto.pharmacy;

import com.aziz.demosec.Entities.DeliveryType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyOrderRequestDTO {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Pharmacy ID is required")
    private Long pharmacyId;

    private Long prescriptionId;

    @NotNull(message = "Delivery address is required")
    private String deliveryAddress;


    private LocalDate scheduledDeliveryDate;

    @NotNull(message = "Prescription image URL is required")
    private String prescriptionImageUrl;

    @NotNull(message = "Order items cannot be empty")
    @Valid
    private List<OrderItemRequestDTO> items;

    @NotNull(message = "Delivery type is required")
    private DeliveryType deliveryType;
}
