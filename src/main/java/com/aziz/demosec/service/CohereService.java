package com.aziz.demosec.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.List;

@Service
public class CohereService {

    @Value("${cohere.api.key}")
    private String apiKey;

    @Value("${cohere.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String summarize(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            return "No medical data available to summarize.";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "message", "Summarize the following patient medical record in professional clinical language. Be concise but thorough.\n\n" + rawText,
                "model", "command-r-plus-08-2024"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

            if (response.getBody() == null || !response.getStatusCode().is2xxSuccessful()) {
                return "Error: Unable to generate summary with Cohere Chat API at this time.";
            }

            Map<String, Object> responseBody = response.getBody();
            // In Chat API, the generated text is in the 'text' field
            String summary = (String) responseBody.get("text");
            
            return (summary != null) ? summary : "No summary returned by Cohere.";
        } catch (Exception e) {
            System.err.println("Cohere API Error: " + e.getMessage());
            return "Error calling Cohere service: " + e.getMessage();
        }
    }
}