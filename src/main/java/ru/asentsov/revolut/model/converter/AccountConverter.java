package ru.asentsov.revolut.model.converter;

import ru.asentsov.revolut.model.entity.AccountEntity;
import ru.asentsov.revolut.model.view.AccountView;

public class AccountConverter {
    public static AccountView convertAccountEntityToView(AccountEntity accountEntity) {
        return new AccountView(accountEntity.getAccountId(), accountEntity.getBalance());
    }
}
