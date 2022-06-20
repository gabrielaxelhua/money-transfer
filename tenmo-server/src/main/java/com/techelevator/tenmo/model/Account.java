package com.techelevator.tenmo.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Account {

    private Long id;

    private Long userId;

    private String username;

    private BigDecimal balance;

}
