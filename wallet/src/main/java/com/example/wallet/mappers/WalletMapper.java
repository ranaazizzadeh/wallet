package com.example.wallet.mappers;

import com.example.wallet.models.dtos.wallet.WalletRequestDto;
import com.example.wallet.models.dtos.wallet.WalletResponseDto;
import com.example.wallet.models.entities.Wallet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    WalletResponseDto entityToDto(Wallet walletEntity);
    Wallet dtoToEntity(WalletRequestDto walletRequestDto);

    List<WalletResponseDto> entityListToDtoList (List<Wallet> walletEntities);
    List<Wallet> dtoListToEntityList (List<WalletRequestDto> walletRequestDtos);
}
