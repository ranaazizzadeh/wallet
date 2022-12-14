package com.example.wallet.services;

import com.example.wallet.models.entities.User;
import com.example.wallet.models.entities.Wallet;
import com.example.wallet.models.enums.WalletStatus;

import java.util.List;

public interface WalletService {
    Wallet create(Wallet walletEntity);

    Wallet changeWalletStatus(String walletName, WalletStatus status);

    Wallet increaseBalance(Wallet wallet, Double amount);

    Wallet decreaseBalance(Wallet wallet, Double amount);

    List<Wallet> findUsersWallet();

    Wallet findWalletByUserAndWalletName(User user, String walletName);

}
