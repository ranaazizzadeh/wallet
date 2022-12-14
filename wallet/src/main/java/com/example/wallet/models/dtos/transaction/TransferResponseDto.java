package com.example.wallet.models.dtos.transaction;

import com.example.wallet.models.dtos.wallet.WalletResponseDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TransferResponseDto {

    private WalletResponseDto sourceWallet;
    private WalletResponseDto targetWallet;
}
