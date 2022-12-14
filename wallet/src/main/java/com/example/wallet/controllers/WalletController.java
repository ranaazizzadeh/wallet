package com.example.wallet.controllers;

import com.example.wallet.mappers.TransactionMapper;
import com.example.wallet.mappers.WalletMapper;
import com.example.wallet.models.dtos.transaction.TransactionRequestDto;
import com.example.wallet.models.dtos.transaction.TransactionResponseDto;
import com.example.wallet.models.dtos.transaction.TransferRequestDto;
import com.example.wallet.models.dtos.transaction.TransferResponseDto;
import com.example.wallet.models.dtos.wallet.WalletRequestDto;
import com.example.wallet.models.dtos.wallet.WalletResponseDto;
import com.example.wallet.models.entities.Wallet;
import com.example.wallet.models.enums.WalletStatus;
import com.example.wallet.services.TransactionService;
import com.example.wallet.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;
    private final WalletMapper walletMapper;

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;


    @PostMapping
    public WalletResponseDto createWallet(@Valid @RequestBody WalletRequestDto wallet) {

        Wallet walletEntity = walletService.create(walletMapper.dtoToEntity(wallet));
        return walletMapper.entityToDto(walletEntity);
    }

    @GetMapping
    public List<WalletResponseDto> walletList() {
        return walletMapper
                .entityListToDtoList(walletService.findUsersWallet())
                .stream()
                .sorted(Comparator.comparing(WalletResponseDto::getCreationTime))
                .collect(Collectors.toList());
    }

    @PutMapping("/enable/{walletName}")
    public WalletResponseDto enableWallet(@PathVariable String walletName) {
        return walletMapper.entityToDto(walletService.changeWalletStatus(walletName, WalletStatus.ENABLED));
    }

    @PutMapping("/disable/{walletName}")
    public WalletResponseDto disableWallet(@PathVariable String walletName) {
        return walletMapper.entityToDto(walletService.changeWalletStatus(walletName, WalletStatus.DISABLED));
    }


    @PostMapping("/charge")
    public WalletResponseDto chargeWallet(@Valid @RequestBody TransactionRequestDto transactionRequestDto){
        Wallet wallet = transactionService.chargeWallet(transactionMapper.dtoToEntity(transactionRequestDto));
        return walletMapper.entityToDto(wallet);
    }

    @PostMapping("/withdraw")
    public WalletResponseDto withdrawWallet(@Valid @RequestBody TransactionRequestDto transactionRequestDto){
        Wallet wallet = transactionService.withdrawWallet(transactionMapper.dtoToEntity(transactionRequestDto));
        return walletMapper.entityToDto(wallet);
    }

    @PostMapping("/transfer")
    public TransferResponseDto transfer(@Valid @RequestBody TransferRequestDto transferRequest){
        return transactionService.transfer(transferRequest);
    }

    @GetMapping("/{walletName}/transactions")
    public List<TransactionResponseDto> getTransaction(@PathVariable String walletName){
        return transactionMapper
                .entityListToDtoList(transactionService.findTransactions(walletName))
                .stream()
                .sorted(Comparator.comparing(TransactionResponseDto::getTime))
                .collect(Collectors.toList());
    }
}
