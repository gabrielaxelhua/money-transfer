package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
public class Transfer {

    private Long id;

    private Long transferTypeId;

    private Long transferStatusId;

    private Long accountFrom;

    private Long accountTo;

    private BigDecimal amount;
}
