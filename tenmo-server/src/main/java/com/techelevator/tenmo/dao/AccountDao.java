package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public interface AccountDao {

    List<Account> findAll();

    Account findByUserId(Long id) throws AccountNotFoundException;

    Account findByAccountId(Long id) throws AccountNotFoundException;

    void changeAmount(Long id, BigDecimal amount);

    BigDecimal getBalance(Long userId);
}
