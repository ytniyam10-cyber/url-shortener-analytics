package com.example.url_shortener.service;

import com.example.url_shortener.DTO.AnalyticsResponse;
import com.example.url_shortener.entity.UrlMapping;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.repo.ClickEventRepository;
import com.example.url_shortener.repo.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AnalyticsService {
    @Autowired
    private UrlMappingRepository urlMappingRepository;
    @Autowired
    private ClickEventRepository clickEventRepository;

    public AnalyticsResponse getAnalytics(String username,String shortCode){
        UrlMapping mapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Short code not found"));
        if(!mapping.getCreatedBy().getUsername().equals(username)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this link");
        }
        long totalClicks = clickEventRepository.countByShortCode(shortCode);

        Map<String,Long>clicksByDay=toMap(clickEventRepository.countClicksByDay(shortCode));
        Map<String,Long> topReferrers = toMap(clickEventRepository.countClicksByReferrer(shortCode));
        Map<String,Long>topBrowsers = toMap(clickEventRepository.countClicksByBrowser(shortCode));

        return new AnalyticsResponse(shortCode, totalClicks, clicksByDay, topReferrers, topBrowsers);
    }
    private Map<String, Long> toMap(List<Map<String, Object>> mongoRows) {
        Map<String, Long> resultMap = new HashMap<>();
        for (Map<String, Object> row : mongoRows) {
            String key = row.get("_id") != null ? row.get("_id").toString() : "Unknown";
            Number countNumber = (Number) row.get("count");
            Long count = countNumber != null ? countNumber.longValue() : 0L;
            resultMap.put(key, count);
        }
        return resultMap;
    }

}
