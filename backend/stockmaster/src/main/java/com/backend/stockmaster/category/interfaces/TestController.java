package com.backend.stockmaster.category.interfaces;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/categories")
    public Map<String, Object> testCategories() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "API de test fonctionnelle");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}