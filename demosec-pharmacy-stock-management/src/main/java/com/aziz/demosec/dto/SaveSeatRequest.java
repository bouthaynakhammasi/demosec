package com.aziz.demosec.dto;

import lombok.Data;

@Data
public class SaveSeatRequest {
    private Long id; // Null if new seat
    private String zoneName;
    private String seatLabel;
    private Double posX;
    private Double posY;
    private String status; // AVAILABLE, BLOCKED
}
