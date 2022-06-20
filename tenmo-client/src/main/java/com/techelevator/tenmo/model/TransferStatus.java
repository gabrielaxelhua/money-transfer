package com.techelevator.tenmo.model;

import lombok.Data;

@Data
public class TransferStatus {
    private Long id;
    private String transferStatus;

    /*public TransferStatus() {}

    public TransferStatus(Long id, String transferStatus) {
        this.id = id;
        this.transferStatus = transferStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }*/
}
