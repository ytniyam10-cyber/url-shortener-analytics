package com.example.url_shortener.service;

import com.example.url_shortener.Security.JwtUtil;
import com.example.url_shortener.entity.User;
import com.example.url_shortener.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    @Autowired
    public UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;


    public void register(String username,String password){
        if(userRepository.existsByUsername(username)){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"username alrealy exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

    }
    public String login(String username, String password){
        User user = userRepository.findByUsername(username).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND,"user not found")
        );
        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"invalid password");
        }
        return jwtUtil.generateToken(username);
    }
}
