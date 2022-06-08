package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, tu.user_id, tu.username, balance FROM tenmo_user tu JOIN account a ON tu.user_id = a.user_id;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Account account = mapRowToAccount(results);
            accounts.add(account);
        }
        return accounts;
    }


    @Override
    public Account findByUsername(String username) throws AccountNotFoundException {
//        Account account = null;
        String sql = "SELECT account_id, tu.user_id, tu.username, balance FROM tenmo_user tu JOIN account a ON tu.user_id = a.user_id WHERE username ILIKE ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        if (results.next()){
//            account = mapRowToAccount(results);
            return mapRowToAccount(results);
        }
        throw new AccountNotFoundException("Account not found");
    }

    @Override
    public Account update(Account account, int id) throws AccountNotFoundException {
        return null;
    }

    @Override
    public void delete(int id) throws AccountNotFoundException {

    }

    private Account mapRowToAccount(SqlRowSet results) {
        Account account = new Account();
        account.setAccount_id(results.getInt("account_id"));
        account.setUser_id(results.getInt("user_id"));
        account.setUsername(results.getString("username"));
        account.setBalance(results.getBigDecimal("balance"));
        return account;
    }
}
