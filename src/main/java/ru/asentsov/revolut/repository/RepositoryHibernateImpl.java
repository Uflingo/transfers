package ru.asentsov.revolut.repository;

import lombok.extern.java.Log;
import org.glassfish.grizzly.utils.Pair;
import org.jvnet.hk2.annotations.Service;
import ru.asentsov.revolut.model.entity.AccountEntity;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

@Log
@Service
public class RepositoryHibernateImpl implements IRepository {

    private final EntityManagerFactory emf;

    @Inject
    public RepositoryHibernateImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Creates an account with zero balance
     *
     * @return created account as AccountEntity
     */
    public AccountEntity createAccount() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            AccountEntity accountEntity = new AccountEntity();
            accountEntity.setBalance(BigDecimal.ZERO.setScale(2));
            em.persist(accountEntity);
            em.getTransaction().commit();
            return accountEntity;
        } catch (Exception ex) {
            log.log(Level.SEVERE, "createAccount exception", ex);
            em.getTransaction().rollback();
            throw ex;
        }
        finally {
            em.close();
        }
    }

    /**
     * Finds account by id
     * @param accountId
     * @return account or null if not found
     */
    public AccountEntity getAccount(int accountId) {
        EntityManager em = emf.createEntityManager();
        AccountEntity accountEntity = em.find(AccountEntity.class, accountId);
        em.close();
        return accountEntity;
    }

    /**
     * Saves account to db
     * @param accountEntity
     * @return refreshed account
     */
    public AccountEntity saveAccount(AccountEntity accountEntity) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            AccountEntity merge = em.merge(accountEntity);
            em.getTransaction().commit();
            return merge;
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Save account exception", ex);
            em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    /**
     * Saves two accounts in one transaction
     *
     * @param fromAccount
     * @param toAccount
     * @return Pair<newFromAccount, newToAccount>
     */
    public Pair<AccountEntity, AccountEntity> saveAccounts(AccountEntity fromAccount,
                                                           AccountEntity toAccount) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            AccountEntity from = em.merge(fromAccount);
            AccountEntity to = em.merge(toAccount);
            em.getTransaction().commit();
            return new Pair(from, to);
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Users transaction exception", ex);
            em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public List<AccountEntity> getAllAccounts() {
        EntityManager em = emf.createEntityManager();
        return em.createQuery("select a from AccountEntity a order by a.accountId", AccountEntity.class)
                .getResultList();
    }

}
