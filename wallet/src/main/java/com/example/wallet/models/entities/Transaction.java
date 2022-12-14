package com.example.wallet.models.entities;

import com.example.wallet.models.enums.TransactionType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Min(0)
    private Double amount;

    @Enumerated(EnumType.ORDINAL)
    private TransactionType type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    @ManyToOne
    @JoinColumn(name = "wallet_id",nullable = false)
    private Wallet wallet;

    @NotBlank
    @Size(min=16,max = 16,message = "card number should have 16 characters")
    private String cardNumber;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    private String description;

}
