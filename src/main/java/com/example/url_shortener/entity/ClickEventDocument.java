package com.example.url_shortener.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "click_events")
public class ClickEventDocument {

    @Id
    private String id;

    private String shortCode;
    private String timestamp;
    private String referrer;
    private String userAgent;
    private String browser;
    private String ipAddress;

    public ClickEventDocument(String shortCode, String timestamp, String referrer,
                              String userAgent,String browser, String ipAddress) {
        this.shortCode = shortCode;
        this.timestamp = timestamp;
        this.referrer = referrer;
        this.userAgent = userAgent;
        this.browser=browser;
        this.ipAddress = ipAddress;
    }
}
