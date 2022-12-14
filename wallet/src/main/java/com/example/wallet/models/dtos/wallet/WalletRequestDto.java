package com.example.wallet.models.dtos.wallet;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class WalletRequestDto {

    @NotBlank(message = "wallet should have name")
    private String name;
    private Long version;

}
