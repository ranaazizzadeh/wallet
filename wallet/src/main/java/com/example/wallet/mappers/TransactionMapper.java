package com.example.wallet.mappers;

import com.example.wallet.models.dtos.transaction.TransactionRequestDto;
import com.example.wallet.models.dtos.transaction.TransactionResponseDto;
import com.example.wallet.models.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target="user", source="transactionEntity.user.username")
    @Mapping(target="walletName", source="transactionEntity.wallet.name")
    TransactionResponseDto entityToDto(Transaction transactionEntity);

    @Mapping(target="wallet.name", source="transactionRequestDto.walletName")
    Transaction dtoToEntity(TransactionRequestDto transactionRequestDto);

    List<TransactionResponseDto> entityListToDtoList (List<Transaction> transactionEntities);
    List<Transaction> dtoListToEntityList (List<TransactionRequestDto> transactionRequestDtos);
}
