package ru.asentsov.revolut.dao;

import lombok.extern.java.Log;
import org.glassfish.grizzly.utils.Pair;
import org.jvnet.hk2.annotations.Service;
import ru.asentsov.revolut.model.TransferResponse;
import ru.asentsov.revolut.model.converter.AccountConverter;
import ru.asentsov.revolut.model.entity.AccountEntity;
import ru.asentsov.revolut.repository.IRepository;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status.*;
import static ru.asentsov.revolut.model.converter.AccountConverter.convertAccountEntityToView;

@Log
@Service
public class AccountsDaoImpl implements IAccountsDao {
    private static final int MAX_UPDATE_RETRIES = 10;
    static final String WRONG_VALUE = "Wrong value";
    static final String NOT_ENOUGH_MONEY = "Not enough money";
    static final String SELF_TRANSFER = "Self transfers are forbidden";

    private final IRepository accountsRepository;

    @Inject
    public AccountsDaoImpl(IRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    public Response createAccount() {
        AccountEntity account = accountsRepository.createAccount();
        log.info("Created an account " + account.getAccountId());
        return Response.status(CREATED)
                .entity(convertAccountEntityToView(account))
                .build();
    }

    public Response getAccount(int accountId) {
        AccountEntity account = accountsRepository.getAccount(accountId);
        if (account == null) {
            return Response.status(NOT_FOUND).build();
        }
        return Response.status(OK)
                .entity(convertAccountEntityToView(account))
                .build();
    }

    public Response putMoneyToAccount(int accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Response.status(BAD_REQUEST)
                    .entity(WRONG_VALUE)
                    .build();
        }
        for (int i = 0; i < MAX_UPDATE_RETRIES; i++) {
            try {
                AccountEntity account = accountsRepository.getAccount(accountId);
                if (account == null) {
                    return Response.status(NOT_FOUND).build();
                }
                account.setBalance(account.getBalance().add(amount));
                AccountEntity accountEntity = accountsRepository.saveAccount(account);
                return Response.status(CREATED).entity(convertAccountEntityToView(accountEntity)).build();
            } catch (OptimisticLockException e) {
                log.log(Level.FINE, "transaction optimistic exception i");
            } catch (Exception ex) {
                log.log(Level.WARNING, "Couldn't get an account", ex);
                return Response.status(INTERNAL_SERVER_ERROR).build();
            }
        }
        return Response.status(INTERNAL_SERVER_ERROR).build();
    }

    public Response createTransfer(int fromAccountId, int toAccountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Response.status(BAD_REQUEST)
                    .entity(WRONG_VALUE)
                    .build();
        }
        if (fromAccountId == toAccountId) {
            return Response.status(BAD_REQUEST)
                    .entity(SELF_TRANSFER)
                    .build();
        }
        for (int i = 0; i < MAX_UPDATE_RETRIES; i++) {
            try {
                AccountEntity fromAccount = accountsRepository.getAccount(fromAccountId);
                AccountEntity toAccount = accountsRepository.getAccount(toAccountId);
                if (fromAccount == null || toAccount == null) {
                    return Response.status(NOT_FOUND).build();
                }
                if (fromAccount.getBalance().compareTo(amount) < 0) {
                    return Response.status(BAD_REQUEST)
                            .entity(NOT_ENOUGH_MONEY)
                            .build();
                }
                fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
                toAccount.setBalance(toAccount.getBalance().add(amount));
                Pair<AccountEntity, AccountEntity> accountsPair = accountsRepository.saveAccounts(fromAccount, toAccount);
                if (accountsPair == null) {
                    log.log(Level.SEVERE, "Empty response from repository");
                    return Response.status(INTERNAL_SERVER_ERROR).build();
                }
                TransferResponse transferResponse = new TransferResponse(amount,
                        convertAccountEntityToView(accountsPair.getFirst()),
                        convertAccountEntityToView(accountsPair.getSecond()));
                return Response.status(CREATED)
                        .entity(transferResponse)
                        .build();
            } catch (OptimisticLockException e) {
                log.log(Level.FINE, "transaction optimistic exception i");
            } catch (Exception ex) {
                log.log(Level.WARNING, "Couldn't get make a transaction", ex);
                return Response.status(INTERNAL_SERVER_ERROR).build();
            }
        }
        return Response.status(INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public Response getAllAccounts() {
        return Response.status(OK)
                .entity(accountsRepository.getAllAccounts()
                        .stream()
                        .map(AccountConverter::convertAccountEntityToView)
                        .collect(Collectors.toList()))
                .build();
    }
}
