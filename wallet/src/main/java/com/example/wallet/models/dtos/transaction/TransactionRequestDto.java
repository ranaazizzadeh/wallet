package com.example.wallet.models.dtos.transaction;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class TransactionRequestDto {

    @NotNull(message = "amount can not be null")
    @Min(0)
    private Double amount;

    @NotBlank(message = "wallet name can not be blank")
    private String walletName;

    @NotBlank(message = "card number can not be blank")
    @Size(min=16,max = 16,message = "card number should have 16 characters")
    private String cardNumber;

}
