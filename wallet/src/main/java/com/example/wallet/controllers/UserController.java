package com.example.wallet.controllers;

import com.example.wallet.mappers.UserMapper;
import com.example.wallet.models.dtos.user.UserRequestDto;
import com.example.wallet.models.dtos.user.UserResponseDto;
import com.example.wallet.models.entities.User;
import com.example.wallet.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserResponseDto signUp(@Valid @RequestBody UserRequestDto user) {

        User userEntity = userService.save(userMapper.dtoToEntity(user));
        return userMapper.entityToDto(userEntity);
    }

    @PutMapping
    public UserResponseDto update(@RequestBody UserRequestDto user) {
        return userMapper.entityToDto(userService.update(userMapper.dtoToEntity(user)));
    }

    @GetMapping("/id/{id}")
    public UserResponseDto findById(@PathVariable Long id) {
        return userMapper.entityToDto(userService.findById(id));
    }

    @GetMapping("/username/{username}")
    public UserResponseDto findByUsername(@PathVariable String username) {
        return userMapper.entityToDto(userService.findByUsername(username));
    }

    @GetMapping
    public List<UserResponseDto> findAll() {
        return   userMapper.entityListToDtoList(userService.find());
    }


}
