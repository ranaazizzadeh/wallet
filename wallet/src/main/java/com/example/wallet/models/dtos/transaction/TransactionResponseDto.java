package com.example.wallet.models.dtos.transaction;

import com.example.wallet.models.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionResponseDto {

    private Long id;
    private Double amount;
    private TransactionType type;
    private String walletName;
    private String cardNumber;
    @JsonFormat(pattern="dd.MM.yyyy HH:mm:ss", timezone="Asia/Tehran")
    private Date time;
    private String user;
    private String description;
}
