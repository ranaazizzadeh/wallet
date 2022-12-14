package com.example.wallet.services;

import com.example.wallet.exceptions.WalletException;
import com.example.wallet.models.entities.User;
import com.example.wallet.models.entities.Wallet;
import com.example.wallet.models.enums.WalletStatus;
import com.example.wallet.repositories.UserRepository;
import com.example.wallet.repositories.WalletRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WalletServiceTest {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

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
    public void shouldReturnCreatedWallet() {
        Wallet wallet = Wallet.builder().name("wallet2").build();
        walletService.create(wallet);

        User user = userService.findByUsername("user1");
        Optional<Wallet> createdWallet = walletRepository.findByUserAndName(user, "wallet2");

        assertTrue(createdWallet.isPresent());
        assertEquals(0l, createdWallet.get().getBalance());
        assertEquals(WalletStatus.ENABLED, createdWallet.get().getEnabled());

    }

    @Test
    public void shouldThrowExceptionInCreatingSameWalletNameForUser() {
        Wallet wallet = Wallet.builder().name("wallet1").build();

        WalletException ex =
                assertThrows(WalletException.class, () -> walletService.create(wallet));
        assertTrue(ex.getMessage().contains("user has a wallet with same name"));

    }

    @Test
    public void shouldReturnChangedWalletStatus() {
        walletService.changeWalletStatus("wallet1", WalletStatus.DISABLED);
        Optional<Wallet> changedWallet = walletRepository.findById(1l);

        assertEquals(WalletStatus.DISABLED,changedWallet.get().getEnabled());
    }

    @Test
    public void shouldThrowExceptionWhenWalletNameDoesNotExistsForUser() {
        WalletException ex =
                assertThrows(WalletException.class, () -> walletService.changeWalletStatus("wallet2", WalletStatus.DISABLED));
        assertTrue(ex.getMessage().contains("wallet doesn't exist"));
    }

    @Test
    public void shouldThrowExceptionWhenAlreadyHasTheStatus() {
        WalletException ex =
                assertThrows(WalletException.class, () -> walletService.changeWalletStatus("wallet1", WalletStatus.ENABLED));
        assertTrue(ex.getMessage().contains("wallet is already " + WalletStatus.ENABLED.toString().toLowerCase()));
    }

    @Test
    public void shouldReturnWalletWithIncreasedBalance() {
        Wallet wallet = walletRepository.findById(1l).get();
        Double oldBalance = wallet.getBalance();
        walletService.increaseBalance(wallet, 200.0);
        Optional<Wallet> changedWallet = walletRepository.findById(1l);

        assertEquals(oldBalance + 200.0,changedWallet.get().getBalance());
    }

    @Test
    public void shouldThrowExceptionInIncreasingBalanceForDisabledWallet() {
        Wallet wallet = walletRepository.findById(1l).get();
        wallet.setEnabled(WalletStatus.DISABLED);
        WalletException ex =
                assertThrows(WalletException.class, () -> walletService.increaseBalance(wallet, 100.0));
        assertTrue(ex.getMessage().contains("wallet is disabled,cannot do any transaction"));
    }

    @Test
    public void shouldReturnWalletWithDecreasedBalance() {
        Wallet wallet = walletRepository.findById(1l).get();
        Double oldBalance = wallet.getBalance();
        walletService.decreaseBalance(wallet, 50.0);
        Optional<Wallet> changedWallet = walletRepository.findById(1l);

        assertEquals(oldBalance - 50.0,changedWallet.get().getBalance());
    }

    @Test
    public void shouldThrowExceptionInDecreasingBalanceForDisabledWallet() {
        Wallet wallet = walletRepository.findById(1l).get();
        wallet.setEnabled(WalletStatus.DISABLED);
        WalletException ex =
                assertThrows(WalletException.class, () -> walletService.decreaseBalance(wallet, 100.0));
        assertTrue(ex.getMessage().contains("wallet is disabled,cannot do any transaction"));
    }

    @Test
    public void shouldThrowExceptionInDecreasingMoreThanBalanceAmount() {
        Wallet wallet = walletRepository.findById(1l).get();
        WalletException ex =
                assertThrows(WalletException.class, () -> walletService.decreaseBalance(wallet, 200.0));
        assertTrue(ex.getMessage().contains("balance is less than withdraw amount"));
    }

    @Test
    public void shouldReturnWalletListOfUser() {
        List<Wallet> usersWallet = walletService.findUsersWallet();
        assertIterableEquals(walletRepository.findAll(), usersWallet);

    }

    @Test
    public void shouldReturnUsersWalletWithThisName() {
        User user = userService.findById(1l);
        Wallet usersWallet = walletService.findWalletByUserAndWalletName(user, "wallet1");
        Wallet wallet = walletRepository.findById(1l).get();

        assertEquals(wallet, usersWallet);
    }

    @Test
    public void shouldThrowExceptionIfUserDoesNotHaveWalletWithThisName() {
        User user = userService.findById(1l);
        WalletException ex =
                assertThrows(WalletException.class, () -> walletService.findWalletByUserAndWalletName(user, "wallet2"));
        assertTrue(ex.getMessage().contains("wallet wallet2 doesn't exist"));
    }

    @AfterEach
    public void deleteData() {
        walletRepository.deleteAll();
        userRepository.deleteAll();
    }

}
