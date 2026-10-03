package com.example.url_shortener.Kafka;

import com.example.url_shortener.DTO.ClickEvent;
import com.example.url_shortener.UserAgentParser;
import com.example.url_shortener.entity.ClickEventDocument;
import com.example.url_shortener.repo.ClickEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsConsumer {

    @Autowired
    private ClickEventRepository clickEventRepository;

    @KafkaListener(topics = "click-events", groupId = "analytics-group")
    public void consume(ClickEvent event) {
        String browser= UserAgentParser.parseBrowser(event.getUserAgent());
        ClickEventDocument document = new ClickEventDocument(
                event.getShortCode(),
                event.getTimestamp(),
                event.getReferrer(),
                event.getUserAgent(),
                browser,
                event.getIpAddress()
        );
        clickEventRepository.save(document);

        System.out.println("Consumed and saved click event for: " + event.getShortCode());
    }
}
