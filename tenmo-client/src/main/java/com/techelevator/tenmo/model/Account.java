package com.techelevator.tenmo.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Account {

    private Long id;
    private Long userId;
    private String username;
    private BigDecimal balance;


    /*public Account() {}

    public Account(Long id, Long userId, String username, BigDecimal balance) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.balance = balance;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }*/
}
