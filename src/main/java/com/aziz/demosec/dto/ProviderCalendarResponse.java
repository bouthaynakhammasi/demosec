package com.aziz.demosec.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProviderCalendarResponse {

    private Long id;
    private Long providerId;
}