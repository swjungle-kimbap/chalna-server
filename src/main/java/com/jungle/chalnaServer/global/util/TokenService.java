package com.jungle.chalnaServer.global.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TokenService {
    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}