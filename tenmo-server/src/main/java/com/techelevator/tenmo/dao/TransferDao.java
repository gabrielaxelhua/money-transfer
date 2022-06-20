package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
import com.techelevator.tenmo.requests.TransferDTO;
import com.techelevator.tenmo.requests.TransferDetails;

import java.util.List;

public interface TransferDao {

    List<Transfer> findAllTransfers();

    Transfer findByTransferId(long id);

    boolean create(Transfer transfer);

    public boolean sendMoney(Transfer transfer) throws AccountNotFoundException;

    List<Transfer> listAllUserTransfers(String username);

    public List<Transfer> getAllPendingTransfers(String username);

    public TransferDetails findTransferDetails(long id);

    public boolean requestTransfer(Transfer transfer) throws AccountNotFoundException;

    public TransferDetails approveTransfer(Transfer transfer) throws AccountNotFoundException;

    public TransferDetails rejectTransfer(Transfer transfer) throws AccountNotFoundException;

    public void updateTransfer(Transfer transfer, int transferStatus);

}
