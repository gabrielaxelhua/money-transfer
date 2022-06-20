package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private UserDao userDao;
    private AccountDao accountDao;

    public AccountController(UserDao userDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    /** ACCOUNTS **/
    /** list all accounts **/
    @GetMapping(path = "/accounts")
    List<Account> listAccounts(){
        return accountDao.findAll();
    }

    /** get single account using the user id **/
    @GetMapping(path = "/account/user/{id}")
    Account findByUserId(@PathVariable Long id) throws AccountNotFoundException {
        return accountDao.findByUserId(id);
    }

    /** USERS **/
    /** list all users **/
    @GetMapping(path = "/users")
    List<User> listUsers(){
        return userDao.findAll();
    }

    /** get single user using the account id **/
    @GetMapping(path = "/user/account/{id}")
    User findByAccountId(@PathVariable Long id) throws AccountNotFoundException {
        return userDao.findByAccountId(id);
    }

    /** get balance using the user id**/
    @GetMapping(path="/balance/{id}/")
    public BigDecimal getBalance(@PathVariable Long id) {
        BigDecimal balance = accountDao.getBalance(id);
        return balance;
    }
}
