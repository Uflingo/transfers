package ru.asentsov.revolut.repository;

import org.glassfish.grizzly.utils.Pair;
import org.jvnet.hk2.annotations.Contract;
import ru.asentsov.revolut.model.entity.AccountEntity;

import java.util.List;

@Contract
public interface IRepository {
    AccountEntity createAccount();
    AccountEntity getAccount(int accountId);
    AccountEntity saveAccount(AccountEntity accountEntity);
    Pair<AccountEntity, AccountEntity> saveAccounts(AccountEntity fromAccount, AccountEntity toAccount);
    List<AccountEntity> getAllAccounts();
}
