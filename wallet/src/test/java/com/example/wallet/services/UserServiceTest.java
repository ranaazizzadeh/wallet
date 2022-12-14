package com.example.wallet.services;

import com.example.wallet.exceptions.WalletException;
import com.example.wallet.models.entities.User;
import com.example.wallet.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbc;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void setup() {
        jdbc.execute("INSERT INTO user_table (id, password, role, username) VALUES ('1', 'password1', 'ROLE_USER', 'user1')");
        jdbc.execute("INSERT INTO user_table (id, password, role, username) VALUES ('2', 'password2', 'ROLE_USER', 'user2')");
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken("user1", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    public void shouldReturnSavedUser() {
        User user = User.builder().username("rana").password("1234").build();
        userService.save(user);

        Optional<User> savedUser = userRepository.findByUsername("rana");


        assertTrue(savedUser.isPresent());
        assertTrue(passwordEncoder.matches("1234", savedUser.get().getPassword()));

    }

    @Test
    void shouldThrowExceptionWhenSaveUserWithDuplicateUsername(){
        User user = new User();
        user.setUsername("user1");
        user.setPassword("1234");

        WalletException ex =
                assertThrows(WalletException.class, () -> userService.save(user));
        assertTrue(ex.getMessage().contains("username should be unique"));
    }


    @Test
    public void shouldReturnUpdatedUser() {

        User user = userRepository.findById(1L).get();
        user.setUsername("rana");
        user.setPassword("1234");

        userService.update(user);

        assertEquals("rana",userRepository.findById(1L).get().getUsername());
        assertTrue(passwordEncoder
                .matches("1234", userRepository.findById(1L).get().getPassword()));
    }


    @Test
    public void shouldThrowExceptionInUpdatingAnotherUser() {
        User user = userRepository.findByUsername("user2").get();
        user.setPassword("1111");

        WalletException ex = assertThrows(WalletException.class, () -> userService.update(user));
        assertTrue(ex.getMessage().contains("only user can update username or password"));
    }


    @Test
    public void shouldReturnListOfAllUsers() {
        List<User> users = userService.find();
        assertIterableEquals(userRepository.findAll(),users);
    }

    @Test
    public void shouldReturnUserWithId() {
        User foundUser = userService.findById(1L);
        assertNotNull(foundUser);
        assertTrue(foundUser.getUsername().equals("user1"));
    }
    @Test
    public void shouldThrowExceptionWhenUserDoesNotExistWithId() {
        WalletException ex =
                assertThrows(WalletException.class, () -> userService.findById(5L));
        assertTrue(ex.getMessage().contains("user not found in db"));
    }

    @Test
    public void shouldReturnUserWithUsername() {
        User foundUser = userService.findByUsername("user1");
        assertNotNull(foundUser);
        assertTrue(foundUser.getId().equals(1L));
    }


    @Test
    public void shouldThrowExceptionWhenUserDoesNotExistWithUsername() {
        WalletException ex =
                assertThrows(WalletException.class, () -> userService.findByUsername("username"));
        assertTrue(ex.getMessage().contains("user username not found in db"));
    }



    @AfterEach
    void deleteData() {
        userRepository.deleteAll();
    }
}
