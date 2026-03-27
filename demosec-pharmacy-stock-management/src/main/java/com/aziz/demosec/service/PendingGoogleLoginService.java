package com.aziz.demosec.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PendingGoogleLoginService {

    // 🔐 Stock temporaire email → role
    private final Map<String, String> map = new ConcurrentHashMap<>();

    // 🔹 Sauvegarder login temporaire
    public void save(String email, String role) {
        map.put(email, role);
    }

    // 🔥 VERSION SAFE (corrige ton bug)
    public String get(String email) {
        return map.getOrDefault(email, "ROLE_PATIENT");
    }

    // 🔹 Supprimer après validation
    public void remove(String email) {
        map.remove(email);
    }
}