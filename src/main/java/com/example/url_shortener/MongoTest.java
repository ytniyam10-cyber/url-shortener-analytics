package com.example.url_shortener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoTest implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    @Value("${spring.data.mongodb.uri:NOT_FOUND}")
    private String mongoUri;

    public MongoTest(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) {
        System.out.println("========== MONGO DIAGNOSTIC ==========");
       // System.out.println("Configured URI in Spring: " + mongoUri);
        System.out.println("Active DB Name: " + mongoTemplate.getDb().getName());
        System.out.println("=======================================");
    }
}
