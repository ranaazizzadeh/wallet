package com.example.wallet.services.Impls;

import com.example.wallet.exceptions.WalletException;
import com.example.wallet.models.entities.User;
import com.example.wallet.models.entities.Wallet;
import com.example.wallet.models.enums.WalletStatus;
import com.example.wallet.repositories.WalletRepository;
import com.example.wallet.services.UserService;
import com.example.wallet.services.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserService userService;


    private User getUser(){
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findByUsername(username);
    }

    @Override
    public Wallet create(Wallet walletEntity){
        walletEntity.setBalance(0.0);
        walletEntity.setEnabled(WalletStatus.ENABLED);
        walletEntity.setUser(getUser());
        walletEntity.setCreationTime(new java.util.Date());
        try {
            Wallet wallet = walletRepository.save(walletEntity);
            return wallet;
        } catch (DataIntegrityViolationException e) {
            log.error("user already has a wallet with same name");
            throw new WalletException("user has a wallet with same name");
        }
    }

    @Override
    public Wallet changeWalletStatus(String walletName, WalletStatus status){
        Wallet wallet =
                walletRepository.findByUserAndName(getUser(), walletName)
                    .orElseThrow(()->new WalletException("wallet doesn't exist"));
        if(wallet.getEnabled().equals(status))
            throw new WalletException("wallet is already " + status.toString().toLowerCase());
        wallet.setEnabled(status);
        walletRepository.save(wallet);
        return wallet;
    }

    @Override
    public Wallet increaseBalance(Wallet wallet, Double amount){

        if (wallet.getEnabled().equals(WalletStatus.DISABLED)){
            log.error("wallet is disabled");
            throw new WalletException("wallet is disabled,cannot do any transaction");
        }
        wallet.setBalance(wallet.getBalance()+amount);
        walletRepository.save(wallet);
        return wallet;
    }

    @Override
    public Wallet decreaseBalance(Wallet wallet, Double amount){
        if (wallet.getEnabled().equals(WalletStatus.DISABLED)){
            log.error("wallet is disabled");
            throw new WalletException("wallet is disabled,cannot do any transaction");
        }
        if(amount>wallet.getBalance()){
            log.error("no enough balance");
            throw new WalletException("balance is less than withdraw amount");
        }
        wallet.setBalance(wallet.getBalance()-amount);
        walletRepository.save(wallet);
        return wallet;
    }

    @Override
    public List<Wallet> findUsersWallet(){
      return   walletRepository.findByUser(getUser());
    }

    @Override
    public Wallet findWalletByUserAndWalletName(User user, String walletName){
        return walletRepository.findByUserAndName(user, walletName)
                        .orElseThrow(()->new WalletException("wallet " + walletName + " doesn't exist"));
    }

}
