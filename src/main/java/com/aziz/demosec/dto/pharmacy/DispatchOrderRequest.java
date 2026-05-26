package com.aziz.demosec.dto.pharmacy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispatchOrderRequest {
    private Long orderId;
    private Long agentId;
}
