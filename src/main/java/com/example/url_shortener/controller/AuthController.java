package com.example.url_shortener.controller;

import com.example.url_shortener.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration and JWT token authentication")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with encrypted password storage.")
    public ResponseEntity<String> authenticate(@RequestBody Map<String,String>mp){
        String username=mp.get("username");
        String password=mp.get("password");
        if(username == null || username.isBlank() || password == null || password.length() < 6){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Username is required and password must be at least 6 characters");
        }
        authService.register(username,password);
        return new ResponseEntity<>("user registered successfully",HttpStatus.CREATED);
    }
    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Validates user credentials and returns a signed JWT Bearer token.")
    public ResponseEntity<Map<String,String>> login(@RequestBody Map<String,String> request){
        String username= request.get("username");
        String password= request.get("password");
        String token = authService.login(username, password);
        return ResponseEntity.ok(Map.of("token",token));
    }

}
