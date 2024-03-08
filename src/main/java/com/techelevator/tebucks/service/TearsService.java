package com.techelevator.tebucks.service;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.*;
import org.jboss.logging.BasicLogger;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
@Component
public class TearsService {


    private RestTemplate restTemplate = new RestTemplate();

    private final String TEARS_BASE_URL = "https://tears.azurewebsites.net";
    private final String LOGIN_ENDPOINT = "/login";
    private final String LOG_TRANSFER_ENDPOINT = "/api/TxLog";
    private String authToken = null;
    private final String username = "msharretts";
    private final String password = "password";

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String loginWithTears() {

        TearsCredentialsDto credentialsDto = new TearsCredentialsDto();
        credentialsDto.setUsername(username);
        credentialsDto.setPassword(password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TearsCredentialsDto> entity = new HttpEntity<>(credentialsDto, headers);
        String token = null;
        try {
            ResponseEntity<TearsTokenDto> response = restTemplate.exchange(TEARS_BASE_URL + LOGIN_ENDPOINT, HttpMethod.POST, entity, TearsTokenDto.class);
            TearsTokenDto body = response.getBody();
            if (body != null) {
                token = body.getToken();
            }
        } catch (RestClientResponseException | ResourceAccessException e) {

        }
        return token;

    }


    public TearsTransferResponseDto logTransfer(TearsTransferDto tearsTransferDto) {

        TearsTransferResponseDto returnedTransfer = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String logTransactionUrl = TEARS_BASE_URL + LOG_TRANSFER_ENDPOINT;
        HttpEntity<TearsTransferDto> entity = makeTransferEntity(tearsTransferDto);

        try {
            ResponseEntity<TearsTransferResponseDto> response = restTemplate.exchange(logTransactionUrl, HttpMethod.POST, entity, TearsTransferResponseDto.class);
            returnedTransfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {

        }
        return returnedTransfer;
    }

    private HttpEntity<TearsTransferDto> makeTransferEntity(TearsTransferDto tearsTransferDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(loginWithTears());
        return new HttpEntity<>(tearsTransferDto, headers);
    }
}




