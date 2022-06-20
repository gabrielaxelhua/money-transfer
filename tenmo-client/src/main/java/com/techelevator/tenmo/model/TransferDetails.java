package com.techelevator.tenmo.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDetails {
    private Long id;

    private String transferType;

    private String transferStatus;

    private String accountFromUsername;

    private String accountToUsername;

    private BigDecimal amount;
}
