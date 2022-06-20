package com.techelevator.tenmo.requests;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDTO {

    private String senderUsername;
    private String receiverUsername;
    BigDecimal amount;

    public TransferDTO(String senderUsername, String receiverUsername, BigDecimal amount) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.amount = amount;
    }
}
