package com.example.url_shortener.service;

import com.example.url_shortener.entity.UrlMapping;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.repo.UrlMappingRepository;
import com.example.url_shortener.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Duration;

@Service
public class UrlShortenerService {
    @Autowired
    private UrlMappingRepository urlMappingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Transactional
    public String shortenUrl(String originalUrl,String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "User not found"));
        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl(originalUrl);
        mapping.setCreatedBy(user);
        UrlMapping saved= urlMappingRepository.save(mapping);
        String encode = encode(saved.getId());
        saved.setShortCode(encode);
        urlMappingRepository.save(saved);
        return encode;
    }
    public String getOriginalUrl(String shortcode){
        String cachedUrl= redisTemplate.opsForValue().get(shortcode);
        if(cachedUrl!=null){
            System.out.println("cache hit!");
            return cachedUrl;
        }
        System.out.println("cache miss!");
        UrlMapping mapping= urlMappingRepository.findByShortCode(shortcode).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Short code not found: " + shortcode));
        redisTemplate.opsForValue().set(shortcode,mapping.getOriginalUrl(), Duration.ofHours(1));
        return mapping.getOriginalUrl();
    }

    private String encode(long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(ALPHABET.charAt((int)(id % 62)));
            id /= 62;
        }
        return sb.reverse().toString();
    }
}
