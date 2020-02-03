package ru.asentsov.revolut.repository;

import org.glassfish.grizzly.utils.Pair;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.asentsov.revolut.configuration.ApplicationBinder;
import ru.asentsov.revolut.model.entity.AccountEntity;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class AccountRepositoryTest {

    private static BigDecimal ZERO = BigDecimal.ZERO.setScale(2);
    private static BigDecimal NEW_AMOUNT = new BigDecimal("5.83");
    private static BigDecimal TRANSFER_AMOUNT = new BigDecimal("1.30");

    @Inject
    private IRepository accountsRepository;

    @Inject
    private EntityManagerFactory emf;

    @Before
    public void setUp() {
        ServiceLocator locator = ServiceLocatorUtilities.bind(new ApplicationBinder());
        locator.inject(this);
    }

    @After
    public void tearDown() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from AccountEntity").executeUpdate();
        em.flush();
        em.clear();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void createAccountTest() {
        AccountEntity account = accountsRepository.createAccount();
        assertNotNull(account);
        assertNotEquals(0, account.getAccountId());
        assertEquals(ZERO, account.getBalance());
    }

    @Test
    public void getAccountTest() {
        AccountEntity account = accountsRepository.getAccount(1);
        assertNull(account);
    }

    @Test
    public void createAndGetAccountTest() {
        AccountEntity account = accountsRepository.createAccount();
        AccountEntity accountGot = accountsRepository.getAccount(account.getAccountId());
        assertNotNull(accountGot);
        assertEquals(account, accountGot);
    }

    @Test
    public void saveAccountTest() {
        AccountEntity account = accountsRepository.createAccount();
        assertNotNull(account);
        assertEquals(ZERO, account.getBalance());

        account.setBalance(NEW_AMOUNT);
        AccountEntity accountEntity = accountsRepository.saveAccount(account);
        assertNotNull(accountEntity);
        assertEquals(NEW_AMOUNT, accountEntity.getBalance());
    }

    @Test
    public void makeTransferTest() {
        AccountEntity account1 = accountsRepository.createAccount();
        assertNotNull(account1);
        AccountEntity account2 = accountsRepository.createAccount();
        assertNotNull(account2);

        account1.setBalance(NEW_AMOUNT);
        account2.setBalance(TRANSFER_AMOUNT);

        Pair<AccountEntity, AccountEntity> accountsPair = accountsRepository.saveAccounts(account1, account2);
        assertEquals(account1.getAccountId(), accountsPair.getFirst().getAccountId());
        assertEquals(account1.getBalance(), accountsPair.getFirst().getBalance());
        assertNotEquals(account1.getVersion(), accountsPair.getFirst().getVersion());

        assertEquals(account2.getAccountId(), accountsPair.getSecond().getAccountId());
        assertEquals(account2.getBalance(), accountsPair.getSecond().getBalance());
        assertNotEquals(account2.getVersion(), accountsPair.getSecond().getVersion());
    }

    @Test
    public void getAllAccountsTest_emptyList() {
        assertEquals(0, accountsRepository.getAllAccounts().size());
    }

    @Test
    public void getAllAccountsTest_multipleAccounts() {
        accountsRepository.createAccount();
        accountsRepository.createAccount();

        assertEquals(2, accountsRepository.getAllAccounts().size());
    }
}
