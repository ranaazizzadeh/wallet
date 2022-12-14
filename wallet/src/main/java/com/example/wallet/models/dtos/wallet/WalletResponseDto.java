package com.example.wallet.models.dtos.wallet;

import com.example.wallet.models.enums.WalletStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class WalletResponseDto {
    private Long id;
    private String name;
    private WalletStatus enabled;
    private Double balance;
    @JsonFormat(pattern="dd.MM.yyyy HH:mm:ss", timezone="Asia/Tehran")
    private Date creationTime;
    private Long version;
}
