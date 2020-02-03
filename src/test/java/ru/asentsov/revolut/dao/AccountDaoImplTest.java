package ru.asentsov.revolut.dao;

import org.glassfish.grizzly.utils.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.asentsov.revolut.model.entity.AccountEntity;
import ru.asentsov.revolut.model.view.AccountView;
import ru.asentsov.revolut.repository.IRepository;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountDaoImplTest {
    @Mock
    private IRepository repositoryMock;

    @InjectMocks
    private AccountsDaoImpl accountsDao;

    @Test
    public void createAccountTest_success() {
        final int accountId = 1;
        when(repositoryMock.createAccount()).thenReturn(new AccountEntity(accountId, new BigDecimal("0.00"), 0));
        Response account = accountsDao.createAccount();
        assertEquals(Response.Status.CREATED.getStatusCode(), account.getStatus());
        assertNotNull(account.getEntity());
        assertEquals(accountId, ((AccountView)account.getEntity()).getAccountId());
    }

    @Test
    public void getAccountTest_success() {
        final int accountId = 1;
        when(repositoryMock.getAccount(accountId)).thenReturn(new AccountEntity(accountId, new BigDecimal("0.00"), 0));
        Response account = accountsDao.getAccount(accountId);
        assertEquals(Response.Status.OK.getStatusCode(), account.getStatus());
        assertNotNull(account.getEntity());
        assertEquals(accountId, ((AccountView)account.getEntity()).getAccountId());
    }

    @Test
    public void putMoneyToAccountTest_noAccount() {
        Response response = accountsDao.putMoneyToAccount(1, BigDecimal.TEN);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void putMoneyToAccountTest_wrongAmount() {
        Response response = accountsDao.putMoneyToAccount(1, BigDecimal.ZERO);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(AccountsDaoImpl.WRONG_VALUE, response.getEntity());
    }

    @Test
    public void putMoneyToAccountTest_success() {
        final int accountId = 1;
        final BigDecimal amount = new BigDecimal("103.00");
        AccountEntity accountEntity = new AccountEntity(accountId, new BigDecimal("0.00"), 0);
        when(repositoryMock.getAccount(accountId)).thenReturn(accountEntity);
        AccountEntity resultAccountEntity = new AccountEntity(accountId, amount, 1);
        when(repositoryMock.saveAccount(accountEntity)).thenReturn(resultAccountEntity);

        Response response = accountsDao.putMoneyToAccount(accountId, amount);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(accountId, ((AccountView)response.getEntity()).getAccountId());
        assertEquals(amount, ((AccountView)response.getEntity()).getBalance());
    }

    @Test
    public void createTransferTest_wrongAmount() {
        Response response = accountsDao.createTransfer(1, 2, BigDecimal.ZERO);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(AccountsDaoImpl.WRONG_VALUE, response.getEntity());
    }

    @Test
    public void createTransferTest_selfTransfer() {
        Response response = accountsDao.createTransfer(1, 1, BigDecimal.TEN);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(AccountsDaoImpl.SELF_TRANSFER, response.getEntity());
    }

    @Test
    public void createTransferTest_notEnoughMoney() {
        final int accountId1 = 3;
        final int accountId2 = 4;
        when(repositoryMock.getAccount(accountId1)).thenReturn(new AccountEntity(accountId1, new BigDecimal("0.00"), 0));
        when(repositoryMock.getAccount(accountId2)).thenReturn(new AccountEntity(accountId2, new BigDecimal("0.00"), 0));

        Response response = accountsDao.createTransfer(accountId1, accountId2, BigDecimal.ONE);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(AccountsDaoImpl.NOT_ENOUGH_MONEY, response.getEntity());
    }

    @Test
    public void createTransferTest_success() {
        final int accountId1 = 5;
        final int accountId2 = 6;

        final BigDecimal amount = new BigDecimal("1.00");
        BigDecimal balance1 = new BigDecimal("10.00");
        BigDecimal balance2 = new BigDecimal("0.00");

        when(repositoryMock.getAccount(accountId1)).thenReturn(new AccountEntity(accountId1, balance1, 0));
        when(repositoryMock.getAccount(accountId2)).thenReturn(new AccountEntity(accountId2, balance2, 0));

        AccountEntity account1After = new AccountEntity(accountId1, balance1.subtract(amount), 1);
        AccountEntity account2After = new AccountEntity(accountId2, balance2.add(amount), 1);
        when(repositoryMock.saveAccounts(any(), any())).thenReturn(new Pair<>(account1After, account2After));

        Response response = accountsDao.createTransfer(accountId1, accountId2, amount);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    public void getAllAccountsTest() {
        when(repositoryMock.getAllAccounts()).thenReturn(List.of(new AccountEntity(1, BigDecimal.ZERO, 0),
                new AccountEntity(2, BigDecimal.ZERO, 0)));

        Response allAccounts = accountsDao.getAllAccounts();
        assertNotNull(allAccounts);
        assertEquals(2, ((List<AccountView>)allAccounts.getEntity()).size());
    }
}
