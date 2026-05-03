package com.aziz.demosec.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PredictionService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String AI_URL = "http://localhost:8000/predict";

    public Map predict(Map<String, Object> data) {
        return restTemplate.postForObject(AI_URL, data, Map.class);
    }
}