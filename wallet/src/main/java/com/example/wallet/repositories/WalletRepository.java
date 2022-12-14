package com.example.wallet.repositories;

import com.example.wallet.models.entities.User;
import com.example.wallet.models.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet,Long> {

    List<Wallet> findByUser(User user);

    Optional<Wallet> findByUserAndName(User user, String name);
}
