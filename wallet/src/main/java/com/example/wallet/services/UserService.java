package com.example.wallet.services;

import com.example.wallet.models.entities.User;

import java.util.List;

public interface UserService {
    User save(User user);

    User update(User userEntity);

    List<User> find();

    User findById(Long id);

    User findByUsername(String username);
}
