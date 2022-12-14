package com.example.wallet.repositories;

import com.example.wallet.models.entities.Transaction;
import com.example.wallet.models.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    List<Transaction> findByWallet(Wallet wallet);
}
