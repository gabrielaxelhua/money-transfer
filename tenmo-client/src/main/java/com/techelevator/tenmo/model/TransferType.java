package com.techelevator.tenmo.model;

import lombok.Data;

@Data
public class TransferType {
    private Long id;
    private String transferType;

    /*public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public TransferType(Long id, String transferType) {
        this.id = id;
        this.transferType = transferType;
    }

    public TransferType() {}*/
}
