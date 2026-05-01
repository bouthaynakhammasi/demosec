package com.aziz.demosec.dto;

import com.aziz.demosec.entities.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatResponse {
    private Long id;
    private Long eventId;
    private String zoneName;
    private String seatLabel;
    private Double posX;
    private Double posY;
    private SeatStatus status;
    private String reservedByFullName;
}
