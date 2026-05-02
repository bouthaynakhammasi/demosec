package com.aziz.demosec.dto;

public record HealthMetricsRequest(
    Double height,
    Double weight,
    String glucoseRate
) {}
