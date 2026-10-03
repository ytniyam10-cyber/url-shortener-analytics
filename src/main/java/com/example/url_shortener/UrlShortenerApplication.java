package com.example.url_shortener;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;


@SpringBootApplication
public class UrlShortenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlShortenerApplication.class, args);
	}
	@Autowired
	private MongoTemplate mongoTemplate;

	@PostConstruct
	public void checkMongo() {
		System.out.println("Mongo Database: " + mongoTemplate.getDb().getName());
	}

}
