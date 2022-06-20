package com.techelevator.tenmo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;


@Data
public class TransferStatus {

    private Long id;

    private String transferStatus;

}
