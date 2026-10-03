package com.example.url_shortener.controller;

import com.example.url_shortener.DTO.AnalyticsResponse;
import com.example.url_shortener.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics Engine", description = "Aggregated link performance and telemetry metrics")
public class AnalyticsController {
    @Autowired
    private AnalyticsService analyticsService;

    @Operation(summary = "Get link analytics", description = "Retrieves total clicks, clicks by date, " +
            "top referrers, and top browsers for a link owned by the authenticated user.")
    @GetMapping("/{shortCode}")
    public ResponseEntity<AnalyticsResponse> getAnalytics(@PathVariable String shortCode, Authentication authentication){
        String username= authentication.getName();
        AnalyticsResponse analytics = analyticsService.getAnalytics(username, shortCode);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }
}
