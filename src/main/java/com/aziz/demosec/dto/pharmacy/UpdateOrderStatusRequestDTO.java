package com.aziz.demosec.dto.pharmacy;

import com.aziz.demosec.Entities.PharmacyOrderStatus;
import com.aziz.demosec.Entities.DeliveryType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequestDTO {

    @NotNull(message = "Status is required")
    private PharmacyOrderStatus status;

    private String note;

    private String changedBy;

    private DeliveryType deliveryType; // required when status = AWAITING_CHOICE step
}
