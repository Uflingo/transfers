package ru.asentsov.revolut.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class AccountEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int accountId;
    @Column(scale = 2)
    private BigDecimal balance;

    @Version
    private long version;
}
