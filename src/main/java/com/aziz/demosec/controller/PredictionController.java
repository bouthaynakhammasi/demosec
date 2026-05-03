package com.aziz.demosec.controller;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.dto.*;
import com.aziz.demosec.repository.*;
import com.aziz.demosec.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @PostMapping("/predict")
    public ResponseEntity<?> predict(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(predictionService.predict(body));
    }
}