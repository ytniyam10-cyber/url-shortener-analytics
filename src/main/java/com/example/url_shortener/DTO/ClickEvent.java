package com.example.url_shortener.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClickEvent {
    private String shortCode;
    private String timestamp;
    private String ipAddress;
    private String userAgent;
    private String referrer;

    public ClickEvent(String shortCode, String userAgent, String ipAddress,String referrer) {
        this.shortCode = shortCode;
        this.timestamp = LocalDateTime.now().toString();
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.referrer=referrer;
    }
}
