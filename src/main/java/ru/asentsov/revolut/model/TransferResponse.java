package ru.asentsov.revolut.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.asentsov.revolut.model.view.AccountView;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {
    private BigDecimal amount;
    private AccountView from;
    private AccountView to;
}
