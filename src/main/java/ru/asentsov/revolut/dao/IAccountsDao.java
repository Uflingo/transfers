package ru.asentsov.revolut.dao;

import org.jvnet.hk2.annotations.Contract;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;

@Contract
public interface IAccountsDao {
    Response createAccount();
    Response getAccount(int accountId);
    Response putMoneyToAccount(int accountId, BigDecimal amount);
    Response createTransfer(int fromAccountId, int toAccountId, BigDecimal amount);
    Response getAllAccounts();
}
