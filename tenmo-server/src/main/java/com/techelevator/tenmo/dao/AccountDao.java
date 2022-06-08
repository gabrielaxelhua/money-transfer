package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

import java.util.List;

public interface AccountDao {

    List<Account> findAll();

    Account findByUsername(String username) throws AccountNotFoundException;

    Account update(Account account, int id) throws AccountNotFoundException;

    void delete(int id) throws AccountNotFoundException;

}
