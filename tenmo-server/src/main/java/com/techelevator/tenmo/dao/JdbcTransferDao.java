package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
import com.techelevator.tenmo.requests.TransferDTO;
import com.techelevator.tenmo.requests.TransferDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private final AccountDao accountDao;
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(AccountDao accountDao, JdbcTemplate jdbcTemplate) {
        this.accountDao = accountDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    /** ALL GET METHODS**/

    /** ALL TRANSFERS IN SYSTEM**/
    @Override
    public List<Transfer> findAllTransfers() {
        List<Transfer> transfers = new ArrayList<>();
        String sql =
                "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    /** ALL TRANSFERS CONSTRAINED TO USER **/
    @Override
    public List<Transfer> listAllUserTransfers(String username) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer t " +
                "JOIN account AS sender ON t.account_from = sender.account_id " +
                "JOIN account AS receiver ON t.account_to = receiver.account_id " +
                "JOIN tenmo_user tu ON sender.user_id = tu.user_id OR receiver.user_id = tu.user_id " +
                "WHERE username ILIKE ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        while (results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    /** ALL PENDING TRANSFERS CONSTRAINED TO USER **/
    @Override
    public List<Transfer> getAllPendingTransfers(String username) {
        List<Transfer> pendingTransfers = new ArrayList<>();
        String sql = "SELECT t.transfer_id, t.transfer_type_id, t.transfer_status_id, t.account_from, t.account_to, t.amount " +
                "FROM transfer t " +
                "JOIN account AS sender ON t.account_from = sender.account_id " +
                "JOIN account AS receiver ON t.account_to = receiver.account_id " +
                "JOIN tenmo_user tu ON sender.user_id = tu.user_id OR receiver.user_id = tu.user_id " +
                "JOIN transfer_type tt USING(transfer_type_id) " +
                "JOIN transfer_status ts USING(transfer_status_id) " +
                "WHERE transfer_status_id = 1 AND username ILIKE ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        while (results.next()) {
            pendingTransfers.add(mapRowToTransfer(results));
        }
        return pendingTransfers;
    }

    /** SINGLE TRANSFER WITH TRANSFER ID **/
    @Override
    public Transfer findByTransferId(long id) {
        Transfer transfer = new Transfer();
        String sql =
                "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                        "FROM transfer t " +
                        "JOIN account AS sender ON t.account_from = sender.account_id " +
                        "JOIN account AS receiver ON t.account_to = receiver.account_id " +
                        "WHERE transfer_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        if (result.next()) {
            transfer = mapRowToTransfer(result);
        }
        return transfer;
    }

    /** TRANSFER DETAILS WITH TRANSFER ID **/
    @Override
    public TransferDetails findTransferDetails(long id) {
        TransferDetails transferDetails = new TransferDetails();
        String sql =
                "SELECT transfer_id, transfer_type_desc, transfer_status_desc, " +
                    "sender_user.username as sender_username, receiver_user.username as receiver_username, amount " +
                "FROM transfer t " +
                "JOIN transfer_type tt ON t.transfer_type_id = tt.transfer_type_id " +
                "JOIN transfer_status ts ON t.transfer_status_id = ts.transfer_status_id " +
                "JOIN account as sender_account ON t.account_from = sender_account.account_id " +
                "JOIN tenmo_user AS sender_user ON sender_account.user_id = sender_user.user_id " +
                "JOIN account as receiver_account ON t.account_to = receiver_account.account_id " +
                "JOIN tenmo_user AS receiver_user ON receiver_account.user_id = receiver_user.user_id " +
                "WHERE transfer_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        if (result.next()) {
            transferDetails = mapRowToDetails(result);
        }
        return transferDetails;
    }

    /** CREATE NEW TRANSFER **/
    @Override
    public boolean create(Transfer newTransfer) {
        boolean success = true;
        String sql =
                "INSERT INTO transfer (" +
                        "transfer_type_id, " +
                        "transfer_status_id, " +
                        "account_from, " +
                        "account_to, " +
                        "amount) " +
                        "VALUES (?, ?, ?, ?, ?);";
        int rows = jdbcTemplate.update(sql,
                newTransfer.getTransferTypeId(),
                newTransfer.getTransferStatusId(),
                newTransfer.getAccountFrom(),
                newTransfer.getAccountTo(),
                newTransfer.getAmount());
        if (rows != 1) {
            success = false;
        }
        return success;
    }

    /**
     * SEND TRANSFER
     * changes account balances and
     * calls method to create new transfer
     * **/
    @Override
    @Transactional
    public boolean sendMoney(Transfer transfer) throws AccountNotFoundException {
        Account sender = accountDao.findByAccountId(transfer.getAccountFrom());
        Account receiver = accountDao.findByAccountId(transfer.getAccountTo());
        BigDecimal amount = transfer.getAmount();

        accountDao.changeAmount(sender.getId(), sender.getBalance().subtract(amount));
        accountDao.changeAmount(receiver.getId(), receiver.getBalance().add(amount));

        return create(transfer);
    }

    /**
     * REQUEST TRANSFER
     * creates new transfer with pending status
     * does not impact account balances
     * **/
    @Override
    @Transactional
    public boolean requestTransfer(Transfer transfer) throws AccountNotFoundException {
        return create(transfer);
    }

    /**
     * APPROVE TRANSFER
     * changes account balances
     * changes transfer status to approved
     * returns updated transfer details
     * **/
    @Override
    @Transactional
    public TransferDetails approveTransfer(Transfer transfer) throws AccountNotFoundException {
        Account requester = accountDao.findByAccountId(transfer.getAccountTo());
        Account currentAccount = accountDao.findByAccountId(transfer.getAccountFrom());
        BigDecimal amount = transfer.getAmount();
        int transferStatus = 2;

        accountDao.changeAmount(requester.getId(), requester.getBalance().add(amount));
        accountDao.changeAmount(currentAccount.getId(), currentAccount.getBalance().subtract(amount));

        updateTransfer(transfer, transferStatus);
        return findTransferDetails(transfer.getId());
    }

    /**
     * REJECT TRANSFER
     * changes transfer status to rejected
     * does not impact account balances
     * returns updated transfer details
     * **/
    @Override
    @Transactional
    public TransferDetails rejectTransfer(Transfer transfer) throws AccountNotFoundException {
        int transferStatus = 3;

        updateTransfer(transfer, transferStatus);
        return findTransferDetails(transfer.getId());
    }

    /**
     * UPDATE TRANSFER
     * used by approve and reject transfer to update transfer status
     * **/
    @Override
    public void updateTransfer(Transfer transfer, int transferStatus) {
        String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?;";
        jdbcTemplate.update(sql, transferStatus, transfer.getId());
    }

    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setId(results.getLong("transfer_id"));
        transfer.setTransferTypeId(results.getLong("transfer_type_id"));
        transfer.setTransferStatusId(results.getLong("transfer_status_id"));
        transfer.setAccountFrom(results.getLong("account_from"));
        transfer.setAccountTo(results.getLong("account_to"));
        transfer.setAmount(results.getBigDecimal("amount"));
        return transfer;
    }

    private TransferDetails mapRowToDetails(SqlRowSet results) {
        TransferDetails transferDetails = new TransferDetails();
        transferDetails.setId(results.getLong("transfer_id"));
        transferDetails.setTransferType(results.getString("transfer_type_desc"));
        transferDetails.setTransferStatus(results.getString("transfer_status_desc"));
        transferDetails.setAccountFromUsername(results.getString("sender_username"));
        transferDetails.setAccountToUsername(results.getString("receiver_username"));
        transferDetails.setAmount(results.getBigDecimal("amount"));
        return transferDetails;
    }
}
