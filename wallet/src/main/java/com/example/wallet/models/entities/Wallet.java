package com.example.wallet.models.entities;

import com.example.wallet.models.enums.WalletStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints =
        { @UniqueConstraint(name = "UniqueNameAndUserId", columnNames = { "name", "user_id" })}
       )
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;


    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private WalletStatus enabled;

    @Min(0)
    @Column(nullable = false)
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationTime;

    @Version
    private Long version;

}
