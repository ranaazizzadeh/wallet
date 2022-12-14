package com.example.wallet.models.dtos.transaction;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class TransferRequestDto {
    @NotNull(message = "amount can not be null")
    @Min(0)
    private Double amount;

    @NotBlank(message = "source wallet name can not be blank")
    private String sourceWalletName;

    @NotBlank(message = "target wallet name can not be blank")
    private String targetWalletName;

    private String targetUsername;

}
