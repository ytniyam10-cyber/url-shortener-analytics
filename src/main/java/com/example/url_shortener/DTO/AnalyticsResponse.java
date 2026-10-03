package com.example.url_shortener.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsResponse {
    private String shortCode;
    private long totalClicks;
    private Map<String, Long> clicksByDay;
    private Map<String, Long> topReferrers;
    private Map<String, Long> topBrowsers;
}
