package com.example.wallet.mappers;


import com.example.wallet.models.dtos.user.UserRequestDto;
import com.example.wallet.models.dtos.user.UserResponseDto;
import com.example.wallet.models.entities.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {


    UserResponseDto entityToDto(User userEntity);
    User dtoToEntity(UserRequestDto userRequestDto);

    List<UserResponseDto> entityListToDtoList (List<User> userEntities);
    List<User> dtoListToEntityList (List<UserRequestDto> userRequestDtos);
}
