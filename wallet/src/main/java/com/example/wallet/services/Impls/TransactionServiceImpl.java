package com.example.wallet.services.Impls;

import com.example.wallet.mappers.WalletMapper;
import com.example.wallet.models.dtos.transaction.TransferRequestDto;
import com.example.wallet.models.dtos.transaction.TransferResponseDto;
import com.example.wallet.models.entities.Transaction;
import com.example.wallet.models.entities.User;
import com.example.wallet.models.entities.Wallet;
import com.example.wallet.models.enums.TransactionType;
import com.example.wallet.repositories.TransactionRepository;
import com.example.wallet.services.TransactionService;
import com.example.wallet.services.UserService;
import com.example.wallet.services.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final WalletService walletService;
    private final WalletMapper walletMapper;

    @Value("${escrow.account.card}")
    private String escrowAccountCardNumber;

    private User getUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findByUsername(username);
    }
    private User getTransactionUser(Transaction transactionEntity) {
        User user = transactionEntity.getUser();
        if (user == null) {
            user = getUser();
            transactionEntity.setUser(user);
        }
        return user;
    }

    @Override
    public Transaction addTransaction(Transaction transactionEntity) {
        User user = getTransactionUser(transactionEntity);
        String walletName = transactionEntity.getWallet().getName();
        Wallet wallet = walletService.findWalletByUserAndWalletName(user, walletName);
        transactionEntity.setWallet(wallet);
        transactionEntity.setTime(new Date());
        log.info("add transaction for user:{} and wallet:{}",user.getUsername(),wallet.getName());
        return transactionRepository.save(transactionEntity);
    }

    @Transactional
    @Override
    public Wallet chargeWallet(Transaction transactionEntity) {
        User user = getTransactionUser(transactionEntity);
        transactionEntity.setType(TransactionType.DEPOSIT);
        addTransaction(transactionEntity);
        Wallet wallet = walletService.findWalletByUserAndWalletName(user, transactionEntity.getWallet().getName());
        log.info("charge wallet:{} of user:{}",user.getUsername(),wallet.getName());

        return walletService.
                increaseBalance(
                        wallet,
                        transactionEntity.getAmount()
                );
    }


    @Transactional
    @Override
    public Wallet withdrawWallet(Transaction transactionEntity) {
        User user = getTransactionUser(transactionEntity);
        Wallet wallet = walletService.findWalletByUserAndWalletName(user, transactionEntity.getWallet().getName());
        walletService
                .decreaseBalance(
                        wallet,
                        transactionEntity.getAmount()
                );
        transactionEntity.setType(TransactionType.WITHDRAW);
        addTransaction(transactionEntity);
        log.info("withdraw from wallet:{} of user:{}",user.getUsername(),wallet.getName());

        return wallet;
    }



    @Transactional
    @Override
    public TransferResponseDto transfer(TransferRequestDto transferRequestDto) {
        User sourceUser = getUser();
        Wallet sourceWallet = walletService
                .findWalletByUserAndWalletName(
                        sourceUser,
                        transferRequestDto.getSourceWalletName()
                );
        User targetUser = sourceUser;
        if (transferRequestDto.getTargetUsername() != null)
            targetUser = userService.findByUsername(transferRequestDto.getTargetUsername());
        Wallet targetWallet = walletService
                .findWalletByUserAndWalletName(
                        targetUser,
                        transferRequestDto.getTargetWalletName()
                );
        log.info("transfer from wallet:{} of user:{} to wallet:{} of user:{}"
                ,sourceWallet.getName(),sourceUser.getUsername(),targetWallet.getName(),targetUser.getUsername());

        Transaction withdrawTransactionEntity = Transaction.builder()
                .amount(transferRequestDto.getAmount())
                .wallet(sourceWallet)
                .cardNumber(escrowAccountCardNumber)
                .description("to transfer to walletId: " + targetWallet.getId())
                .build();

        withdrawWallet(withdrawTransactionEntity);
        Transaction depositTransactionEntity = Transaction.builder()
                .amount(transferRequestDto.getAmount())
                .wallet(targetWallet)
                .user(targetUser)
                .cardNumber(escrowAccountCardNumber)
                .description("transferred from walletId: " + sourceWallet.getId())
                .build();
        chargeWallet(depositTransactionEntity);
        return TransferResponseDto.builder()
                .sourceWallet(walletMapper.entityToDto(sourceWallet))
                .targetWallet(walletMapper.entityToDto(targetWallet))
                .build();
    }

    @Override
    public List<Transaction> findTransactions(String walletName){
        Wallet wallet = walletService.findWalletByUserAndWalletName(getUser(), walletName);
        return   transactionRepository.findByWallet(wallet);
    }
}
