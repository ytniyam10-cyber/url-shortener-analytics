package com.example.url_shortener.Kafka;

import com.example.url_shortener.DTO.ClickEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsProducer {
    @Autowired
    private KafkaTemplate<String, ClickEvent> kafkaTemplate;

    public void sendClickEvent(ClickEvent event){
        kafkaTemplate.send("click-events",event.getShortCode(),event);
    }

}
