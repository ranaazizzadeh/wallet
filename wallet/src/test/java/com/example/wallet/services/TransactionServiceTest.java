package com.example.wallet.services;

import com.example.wallet.exceptions.WalletException;
import com.example.wallet.models.dtos.transaction.TransferRequestDto;
import com.example.wallet.models.dtos.transaction.TransferResponseDto;
import com.example.wallet.models.entities.Transaction;
import com.example.wallet.models.entities.User;
import com.example.wallet.models.entities.Wallet;
import com.example.wallet.models.enums.TransactionType;
import com.example.wallet.models.enums.WalletStatus;
import com.example.wallet.repositories.TransactionRepository;
import com.example.wallet.repositories.UserRepository;
import com.example.wallet.repositories.WalletRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.constraints.AssertTrue;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionServiceTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    public void setup() {
        jdbc.execute("INSERT INTO user_table (id, password, role, username) VALUES ('1', 'password1', 'ROLE_USER', 'user1')");
        jdbc.execute("INSERT INTO wallet (id, balance ,creation_time,enabled, name, version,user_id) VALUES ('1', '100','2022-12-12 08:37:56.872000', '1', 'wallet1', '0','1')");

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken("user1", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    public void shouldReturnCreatedTransaction() {
        Wallet wallet = walletRepository.findById(1l).get();

        Transaction transaction = Transaction.builder()
                .amount(100.0).wallet(wallet).type(TransactionType.DEPOSIT)
                .cardNumber("1234123412341234")
                .build();

        transactionService.addTransaction(transaction);
        Optional<Transaction> createdTransaction = transactionRepository.findById(transaction.getId());

        assertTrue(createdTransaction.isPresent());
        assertEquals(createdTransaction.get().getAmount(), 100.0);
        assertEquals(createdTransaction.get().getType(), TransactionType.DEPOSIT);
        assertEquals(createdTransaction.get().getCardNumber(), "1234123412341234");
        assertEquals(createdTransaction.get().getUser().getUsername(), "user1");

    }
    @Test
    public void shouldReturnChargedWallet() {
        Wallet wallet = walletRepository.findById(1l).get();
        Double oldBalance = wallet.getBalance();

        Transaction transaction = Transaction.builder()
                .amount(100.0).wallet(wallet).type(TransactionType.DEPOSIT)
                .cardNumber("1234123412341234")
                .build();

        transactionService.chargeWallet(transaction);
        Optional<Transaction> createdTransaction = transactionRepository.findById(transaction.getId());

        assertTrue(createdTransaction.isPresent());
        assertEquals(createdTransaction.get().getAmount(), 100.0);
        assertEquals(createdTransaction.get().getType(), TransactionType.DEPOSIT);
        assertEquals(createdTransaction.get().getCardNumber(), "1234123412341234");
        assertEquals(createdTransaction.get().getUser().getUsername(), "user1");

        assertEquals(createdTransaction.get().getWallet().getBalance(), oldBalance + 100.0);
    }

    @Test
    public void shouldReturnWithdrawWallet() {
        Wallet wallet = walletRepository.findById(1l).get();
        Double oldBalance = wallet.getBalance();

        Transaction transaction = Transaction.builder()
                .amount(100.0).wallet(wallet).type(TransactionType.WITHDRAW)
                .cardNumber("1234123412341234")
                .build();

        transactionService.withdrawWallet(transaction);
        Optional<Transaction> createdTransaction = transactionRepository.findById(transaction.getId());

        assertTrue(createdTransaction.isPresent());
        assertEquals(createdTransaction.get().getAmount(), 100.0);
        assertEquals(createdTransaction.get().getType(), TransactionType.WITHDRAW);
        assertEquals(createdTransaction.get().getCardNumber(), "1234123412341234");
        assertEquals(createdTransaction.get().getUser().getUsername(), "user1");

        assertEquals(createdTransaction.get().getWallet().getBalance(), oldBalance - 100.0);
    }

    @Test
    public void shouldTransferMoneyBetweenUsersWallets() {
        jdbc.execute("INSERT INTO wallet (id, balance ,creation_time,enabled, name, version,user_id) VALUES ('2', '100','2022-12-12 08:37:56.872000', '1', 'wallet2', '0','1')");
        TransferRequestDto transferRequestDto = TransferRequestDto.builder().amount(50.0).sourceWalletName("wallet1").targetWalletName("wallet2").build();

        transactionService.transfer(transferRequestDto);
        Wallet wallet1 = walletRepository.findById(1l).get();
        Wallet wallet2 = walletRepository.findById(2l).get();

        assertEquals(50.0,wallet1.getBalance());
        assertEquals(150.0,wallet2.getBalance());
        assertEquals("user1", wallet1.getUser().getUsername());
        assertEquals("user1", wallet2.getUser().getUsername());

        List<Transaction> transactionList1 = transactionRepository.findByWallet(wallet1);
        List<Transaction> transactionList2 = transactionRepository.findByWallet(wallet2);


        assertEquals(1,transactionList1.size());
        assertEquals(1,transactionList2.size());
        assertEquals(TransactionType.WITHDRAW,transactionList1.get(0).getType());
        assertEquals(TransactionType.DEPOSIT,transactionList2.get(0).getType());
    }

    @Test
    public void shouldThrowErrorInTransferMoneyBetweenUsersWalletsIfWalletNotExists() {
        TransferRequestDto transferRequestDto = TransferRequestDto.builder().amount(50.0)
                .sourceWalletName("wallet1").targetWalletName("wallet2").build();

        WalletException ex =
                assertThrows(WalletException.class, () ->   transactionService.transfer(transferRequestDto));
        assertTrue(ex.getMessage().contains("wallet wallet2 doesn't exist"));
    }

    @Test
    public void shouldTransferMoneyBetweenTwoUsersWallets() {
        jdbc.execute("INSERT INTO user_table (id, password, role, username) VALUES ('2', 'password2', 'ROLE_USER', 'user2')");
        jdbc.execute("INSERT INTO wallet (id, balance ,creation_time,enabled, name, version,user_id) VALUES ('2', '100','2022-12-12 08:37:56.872000', '1', 'wallet2', '0','2')");

        TransferRequestDto transferRequestDto = TransferRequestDto.builder().amount(50.0)
                .sourceWalletName("wallet1").targetWalletName("wallet2").targetUsername("user2").build();

        transactionService.transfer(transferRequestDto);
        Wallet wallet1 = walletRepository.findById(1l).get();
        Wallet wallet2 = walletRepository.findById(2l).get();

        assertEquals(50.0,wallet1.getBalance());
        assertEquals(150.0,wallet2.getBalance());

        List<Transaction> transactionList1 = transactionRepository.findByWallet(wallet1);
        List<Transaction> transactionList2 = transactionRepository.findByWallet(wallet2);


        assertEquals(1,transactionList1.size());
        assertEquals(1,transactionList2.size());
        assertEquals("user1", transactionList1.get(0).getUser().getUsername());
        assertEquals("user2",transactionList2.get(0).getUser().getUsername());

        assertEquals(TransactionType.WITHDRAW,transactionList1.get(0).getType());
        assertEquals(TransactionType.DEPOSIT,transactionList2.get(0).getType());
    }

    @Test
    public void shouldThrowErrorInTransferMoneyBetweenTwoUsersWalletsIfUserNotExists() {
        TransferRequestDto transferRequestDto = TransferRequestDto.builder().amount(50.0)
                .sourceWalletName("wallet1").targetWalletName("wallet2").targetUsername("user2").build();

        WalletException ex =
                assertThrows(WalletException.class, () ->   transactionService.transfer(transferRequestDto));
        assertTrue(ex.getMessage().contains("user user2 not found in db"));
    }

    @Test
    public void shouldThrowErrorInTransferMoneyBetweenTwoUsersWalletsIfWalletNotExists() {
        jdbc.execute("INSERT INTO user_table (id, password, role, username) VALUES ('2', 'password2', 'ROLE_USER', 'user2')");

        TransferRequestDto transferRequestDto = TransferRequestDto.builder().amount(50.0)
                .sourceWalletName("wallet1").targetWalletName("wallet2").targetUsername("user2").build();

        WalletException ex =
                assertThrows(WalletException.class, () ->   transactionService.transfer(transferRequestDto));
        assertTrue(ex.getMessage().contains("wallet wallet2 doesn't exist"));
    }

    @AfterEach
    public void deleteData() {
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();
    }
}
