package com.techelevator.tenmo.requests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data @Getter @Setter
public class TransferDetails {

    private Long id;

    private String transferType;

    private String transferStatus;

    private String accountFromUsername;

    private String accountToUsername;

    private BigDecimal amount;


}
