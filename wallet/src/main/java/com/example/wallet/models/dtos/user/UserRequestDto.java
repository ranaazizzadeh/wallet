package com.example.wallet.models.dtos.user;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserRequestDto {

    private Long id;

    @NotBlank(message = "user name should not be blank")
    private String username;

    @NotBlank(message = "user name should not be blank")
    private String password;

}
