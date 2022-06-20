package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.w3c.dom.stylesheets.LinkStyle;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**FIND ALL ACCOUNTS**/
    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql =
                "SELECT account_id, tu.user_id, tu.username, balance " +
                "FROM tenmo_user tu JOIN account a ON tu.user_id = a.user_id;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Account account = mapRowToAccount(results);
            accounts.add(account);
        }
        return accounts;
    }

    /** FIND ACCOUNT WITH THE USER ID **/
    @Override
    public Account findByUserId(Long id) throws AccountNotFoundException {
        Account account = null;
        String sql =
                "SELECT account_id, tu.user_id, tu.username, balance " +
                "FROM tenmo_user tu JOIN account a ON tu.user_id = a.user_id " +
                "WHERE tu.user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()){
            account = mapRowToAccount(results);
            return account;
        }
        throw new AccountNotFoundException("Account not found");
    }

    /** FIND ACCOUNT USING ACCOUNT ID **/
    @Override
    public Account findByAccountId(Long id) throws AccountNotFoundException {
        Account account = null;
        String sql = "SELECT account_id, tu.user_id, username, balance FROM account JOIN tenmo_user tu ON account.user_id = tu.user_id WHERE account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()){
            account = mapRowToAccount(results);
            return account;
        }
        throw new AccountNotFoundException("Account not found");
    }

    /** GET BALANCE USING USER ID **/
    @Override
    public BigDecimal getBalance(Long userId) {
        BigDecimal balance = null;
        String sql = "SELECT balance FROM account WHERE user_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql,userId);
        if (result.next()) {
            balance = result.getBigDecimal("balance");
        }
        return balance;
    }

    /** UPDATES ACCOUNT BALANCE BASED ON TRANSFER **/
    @Override
    public void changeAmount(Long id, BigDecimal amount) {
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?;";
        jdbcTemplate.update(sql, amount, id);
    }

    /** CONVENIENCE **/

    private Account mapRowToAccount(SqlRowSet results) {
        Account account = new Account();
        account.setId(results.getLong("account_id"));
        account.setUserId(results.getLong("user_id"));
        account.setUsername(results.getString("username"));
        account.setBalance(results.getBigDecimal("balance"));
        return account;
    }
}
