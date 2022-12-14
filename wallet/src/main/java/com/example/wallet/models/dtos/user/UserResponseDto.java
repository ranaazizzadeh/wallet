package com.example.wallet.models.dtos.user;

import lombok.Data;

@Data
public class UserResponseDto {

    private Long id;
    private String username;
    private String role;

}
