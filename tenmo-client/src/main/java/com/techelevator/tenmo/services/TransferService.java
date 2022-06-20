package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.*;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TransferService {

    private final String API_BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser authenticatedUser;

    /** INSTANTIATES TRANSFER SERVICE UPON LOGIN AND SAVES JWT FOR SESSION **/
    public TransferService(String API_BASE_URL,AuthenticatedUser authenticatedUser) {
        this.API_BASE_URL = API_BASE_URL;
        this.authenticatedUser = authenticatedUser;
    }

    /** GET METHODS **/

    /**
     * ALL TRANSFERS
     * constrained to current user using principal on server side
     * **/
    public Transfer[] getAllTransfers(){
        Transfer[] transfers = null;
        try {
            ResponseEntity<Transfer[]> response =
                    restTemplate.exchange(API_BASE_URL + "transfers/", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

    /**
     *  ALL PENDING TRANSFERS
     * constrained to current user using principal on server side
     * **/
    public Transfer[] getAllPendingTransfers() {
        Transfer[] pendingTransfers = null;
        try {
            ResponseEntity<Transfer[]> response =
                    restTemplate.exchange(API_BASE_URL + "transfers/pending", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            pendingTransfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return pendingTransfers;
    }

    /** SINGLE TRANSFER OBJECT **/
    public Transfer getTransfer(Long id) {
        Transfer transfer = null;
        try {
            ResponseEntity<Transfer> response =
                    restTemplate.exchange(API_BASE_URL + "transfers/" + id, HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    /**
     * SINGLE TRANSFER DETAILS OBJECT
     * shows transfer type and status and usernames
     * instead of transfer type, status, and account ids
     * **/
    public TransferDetails getTransferDetails(Long id) {
        TransferDetails transferDetails = null;
        try {
            ResponseEntity<TransferDetails> response =
                    restTemplate.exchange(API_BASE_URL + "transfers/details/" + id, HttpMethod.GET, makeAuthEntity(), TransferDetails.class);
            transferDetails = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transferDetails;
    }

    /** SEND TRANSFER **/
    public boolean sendTransfer(Transfer newTransfer) {
        boolean success = false;
        try {
            ResponseEntity<Boolean> response =
                    restTemplate.exchange(API_BASE_URL + "transfers/send", HttpMethod.POST, makeTransferHttpEntity(newTransfer), Boolean.class);
            success = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    /** REQUEST TRANSFER **/
    public boolean requestTransfer(Transfer request) {
        boolean success = false;
        try {
            ResponseEntity<Boolean> response =
                    restTemplate.exchange(API_BASE_URL + "transfers/request", HttpMethod.POST, makeTransferHttpEntity(request), Boolean.class);
            success = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    /** APPROVE TRANSFER **/
    public TransferDetails approveTransfer(Transfer transfer) {
        TransferDetails transferDetails = null;
        try {
            ResponseEntity<TransferDetails> response =
                    restTemplate.exchange(API_BASE_URL + "/transfers/request/approve", HttpMethod.PUT, makeTransferHttpEntity(transfer), TransferDetails.class);
            transferDetails = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transferDetails;
    }

    /** REJECT TRANSFER **/
    public TransferDetails rejectTransfer(Transfer transfer) {
        TransferDetails transferDetails = null;
        try {
            ResponseEntity<TransferDetails> response =
                    restTemplate.exchange(API_BASE_URL + "/transfers/request/reject", HttpMethod.PUT, makeTransferHttpEntity(transfer), TransferDetails.class);
            transferDetails = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transferDetails;
    }

    /** CONVENIENCE METHODS **/

    private HttpEntity<Transfer> makeTransferHttpEntity(Transfer transfer){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<Void> makeAuthEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(headers);
    }
}
