package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
import com.techelevator.tenmo.requests.TransferDTO;
import com.techelevator.tenmo.requests.TransferDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/transfers")
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private TransferDao transferDao;

    public TransferController(TransferDao transferDao) {
        this.transferDao = transferDao;
    }

    /**
     * LIST ALL TRANSFERS
     * using Principal on server side handles only calling transfers
     * for the current user only and not all transfers in the system
     * **/
    @RequestMapping(method = RequestMethod.GET)
    List<Transfer> listTransfers(Principal principal) {
        return transferDao.listAllUserTransfers(principal.getName());
    }

    /**
     * LIST ALL PENDING TRANSFERS
     * using Principal on server side handles only calling pending
     * transfers for the current user only and not all pending transfers
     * **/
    @RequestMapping(path = "/pending", method = RequestMethod.GET)
    List<Transfer> getAllPendingTransfers(Principal principal) {
        return transferDao.getAllPendingTransfers(principal.getName());
    }

    /** GET SINGLE TRANSFER WITH THE TRANSFER ID **/
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public Transfer findTransfer(@PathVariable Long id) {
        return transferDao.findByTransferId(id);
    }

    /** GET TRANSFER DETAILS WITH THE TRANSFER ID**/
    @RequestMapping(path = "/details/{id}", method = RequestMethod.GET)
    public TransferDetails findTransferDetails(@PathVariable Long id) {
        return transferDao.findTransferDetails(id);
    }

    /**
     * SEND A TRANSFER
     * changes the balance amounts in both accounts and
     * creates a new transfer record in the transfer table
     * **/
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/send", method = RequestMethod.POST)
    public boolean sendMoney(@RequestBody Transfer transfer) throws AccountNotFoundException {
        return transferDao.sendMoney(transfer);
    }

    /**
     * REQUEST A TRANSFER
     * creates a new transfer record with a pending status
     * but does not impact account balances
     * **/
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public boolean requestTransfer(@RequestBody Transfer transfer) throws AccountNotFoundException {
        return  transferDao.requestTransfer(transfer);
    }

    /**
     * APPROVE A TRANSFER
     * adjusts the balances of both accounds and
     * updates existing transfer with approved status
     * **/
    @RequestMapping(path = "/request/approve", method = RequestMethod.PUT)
    public TransferDetails approveTransfer(@RequestBody Transfer transfer) throws AccountNotFoundException {
        return transferDao.approveTransfer(transfer);
    }

    /**
     * REJECT A TRANSFER
     * updates existing transfer with rejected status
     * but does not impact account balances
     * **/
    @RequestMapping(path = "/request/reject", method = RequestMethod.PUT)
    public TransferDetails rejectTransfer(@RequestBody Transfer transfer) throws AccountNotFoundException {
        return transferDao.rejectTransfer(transfer);
    }
}
