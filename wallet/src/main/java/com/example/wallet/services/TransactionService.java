package com.example.wallet.services;

import com.example.wallet.models.dtos.transaction.TransferRequestDto;
import com.example.wallet.models.dtos.transaction.TransferResponseDto;
import com.example.wallet.models.entities.Transaction;
import com.example.wallet.models.entities.Wallet;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TransactionService {
    Transaction addTransaction(Transaction transactionEntity);

    @Transactional
    Wallet chargeWallet(Transaction transactionEntity);

    @Transactional
    Wallet withdrawWallet(Transaction transactionEntity);

    @Transactional
    TransferResponseDto transfer(TransferRequestDto transferRequestDto);

    List<Transaction> findTransactions(String walletName);
}
