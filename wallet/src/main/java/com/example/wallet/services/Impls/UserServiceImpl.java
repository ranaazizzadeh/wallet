package com.example.wallet.services.Impls;

import com.example.wallet.exceptions.WalletException;
import com.example.wallet.models.entities.User;
import com.example.wallet.repositories.UserRepository;
import com.example.wallet.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public User save(User userEntity) {

        try {
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword().trim()));
            userEntity.setRole("ROLE_USER");
            return userRepository.save(userEntity);
        } catch (DataIntegrityViolationException ex) {
            log.error("username {} should be unique",userEntity.getUsername());
            throw new WalletException("username should be unique");
        }
    }


    @Override
    public User update(User userEntity) {
        if ((userEntity.getId() == null )){
            log.error("userid was null in update");
            throw new WalletException("userid cannot be null in update");
        }

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = findByUsername(username);
        if (user.getId().equals(userEntity.getId())){
            return   save(userEntity);
        }
        else {
            log.error("jwt is for another user");
            throw new WalletException("only user can update username or password");
        }
    }

    @Override
    public List<User> find() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new WalletException("user not found in db"));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new WalletException("user " + username + " not found in db"));
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity= userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found in db"));
        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(userEntity.getUsername(),
                userEntity.getPassword(),
                Arrays.asList(new SimpleGrantedAuthority(userEntity.getRole())));
        return user;
    }
}
