package com.example.url_shortener.controller;

import com.example.url_shortener.DTO.ClickEvent;
import com.example.url_shortener.Kafka.AnalyticsProducer;
import com.example.url_shortener.service.UrlShortenerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "URL Management", description = "URL shortening and high-speed redirection operations")
public class UrlController {
    @Autowired
    private UrlShortenerService urlService;
    @Autowired
    public AnalyticsProducer analyticsProducer;

    @Operation(summary = "Create short URL", description = "Generates a unique short code for a long URL and caches it in Redis.")
    @PostMapping("/api/urls/shorten")
    public ResponseEntity<Map<String,String>> shortenUrl(@RequestBody Map<String,String> map) {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        String longurl = map.get("longUrl");
        String shortcode = urlService.shortenUrl(longurl,username);
        return ResponseEntity.ok(Map.of(
                "shortCode", shortcode,
                "shortUrl", "http://localhost:8080/" + shortcode
        ));
    }

    @Operation(summary = "Redirect short URL", description = "Resolves short code via Redis/MySQL, emits Kafka event asynchronously, and redirects client.")
        @GetMapping("/{shortcode}")
        public ResponseEntity<Void> redirectUrl(@PathVariable String shortcode, HttpServletRequest request){
            String rawReferrer = request.getHeader("Referer");
            String referrer = (rawReferrer != null && !rawReferrer.isBlank()) ? rawReferrer : "Direct";
            String originalUrl= urlService.getOriginalUrl(shortcode);
            String userAgent = request.getHeader("User-Agent");
            String ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.isBlank()) {
                ipAddress = request.getRemoteAddr();
            }
            analyticsProducer.sendClickEvent(new ClickEvent(shortcode,userAgent,ipAddress,referrer));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Location",originalUrl);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        }
    }
