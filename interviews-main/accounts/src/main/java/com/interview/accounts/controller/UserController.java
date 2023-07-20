package com.interview.accounts.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import com.interview.accounts.model.LoginRequest;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class UserController {

    private static final String VALID_USERNAME = "username";
    private static final String VALID_PASSWORD = "password";

    private Map<String, String> activeSessions = new HashMap<>();

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest loginRequest) {
    	log.debug("Calling UserController login():::::>");
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (username.equals(VALID_USERNAME) && password.equals(VALID_PASSWORD)) {
            if (activeSessions.containsKey(username)) {
                String token = activeSessions.get(username);
                return Map.of("token", token);
            } else {
                String token = createToken(username);
                activeSessions.put(username, token);
                return Map.of("token", token);
            }
        } else {
            throw new IllegalArgumentException("Invalid credentials.");
        }
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");

        if (activeSessions.containsKey(username)) {
            activeSessions.remove(username);
            return Map.of("message", "User logged out successfully.");
        } else {
            throw new IllegalArgumentException("User not found or already logged out.");
        }
    }

    
	private String createToken(String username) {
    	
    	String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";

    	Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), 
    	                            SignatureAlgorithm.HS256.getJcaName());
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 120000); // 2 minutes

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(hmacKey)
                .compact();
    }
}
