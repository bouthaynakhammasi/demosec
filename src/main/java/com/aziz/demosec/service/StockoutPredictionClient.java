package com.aziz.demosec.service;

import com.aziz.demosec.dto.StockoutPredictionRequest;
import com.aziz.demosec.dto.StockoutPredictionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockoutPredictionClient {

    private final RestTemplate restTemplate;

    @Value("${stockout.api.url:http://localhost:8000}")
    private String stockoutApiUrl;

    public StockoutPredictionResult predict(StockoutPredictionRequest request) {
        try {
            return restTemplate.postForObject(stockoutApiUrl + "/predict", request, StockoutPredictionResult.class);
        } catch (Exception e) {
            log.warn("Stockout ML service unavailable: {}", e.getMessage());
            return null;
        }
    }
}
