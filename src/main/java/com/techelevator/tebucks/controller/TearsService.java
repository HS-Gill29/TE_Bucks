package com.techelevator.tebucks.controller;

import com.techelevator.tebucks.model.TearsCredentialsDto;
import com.techelevator.tebucks.model.TransactionLog;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
@Component
public class TearsService {


    private RestTemplate restTemplate = new RestTemplate();


    private final String TEARS_BASE_URL = "https://tears.azurewebsites.net";
    private final String LOGIN_ENDPOINT = "/login";
    private final String LOG_TRANSACTION_ENDPOINT = "/logTransaction";

    public String authenticateWithTears(TearsCredentialsDto tearsCredentialsDto) {
        String loginUrl = TEARS_BASE_URL + LOGIN_ENDPOINT;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);
        return response.getBody();
    }

    public void logTransaction(String jwtToken, TransactionLog transaction) {
        String logTransactionUrl = TEARS_BASE_URL + LOG_TRANSACTION_ENDPOINT;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);
        HttpEntity<TransactionLog> request = new HttpEntity<>(transaction, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(logTransactionUrl, request, String.class);
    }
}




