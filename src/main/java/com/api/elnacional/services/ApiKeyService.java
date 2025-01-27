package com.api.elnacional.services;

import com.api.elnacional.repository.ParametroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class ApiKeyService {

    @Autowired
    ParametroRepository parametroRepository;

    public boolean validateApiKey(String apiKey, String signature) {
        if (apiKey == null || signature == null) {
            return false;
        }

        String validApiKey = parametroRepository.findValorByNombre("API_KEY");
        String secretKey = parametroRepository.findValorByNombre("SECRET_KEY");

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            String expectedSignature = Base64.getEncoder().encodeToString(mac.doFinal(apiKey.getBytes()));

            return validApiKey.equals(apiKey) && signature.equals(expectedSignature);
        } catch (Exception e) {
            return false;
        }
    }

    public String generateSignature(String apiKey) {
        try {
            String secretKey = parametroRepository.findValorByNombre("SECRET_KEY");
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            return Base64.getEncoder().encodeToString(mac.doFinal(apiKey.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Error generando la firma", e);
        }
    }
}