package com.techelevator.tebucks.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class TearsTransferResponseDto {
    private String description;
    @JsonProperty("username_from")
    private String usernameFrom;
    @JsonProperty("username_to")
    private String usernameTo;
    private double amount;
    @JsonProperty("log_id")
    private int logId;
    private LocalDateTime createdDate;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsernameFrom() {
        return usernameFrom;
    }

    public void setUsernameFrom(String usernameFrom) {
        this.usernameFrom = usernameFrom;
    }

    public String getUsernameTo() {
        return usernameTo;
    }

    public void setUsernameTo(String usernameTo) {
        this.usernameTo = usernameTo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getLogId() {
        return logId;
    }

    public void setLog_id(int logId) {
        this.logId = logId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
