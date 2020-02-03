package ru.asentsov.revolut.service;

import ru.asentsov.revolut.dao.IAccountsDao;
import ru.asentsov.revolut.model.PutMoneyRequest;
import ru.asentsov.revolut.model.TransferRequest;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("accounts")
public class TransfersService {
    private final IAccountsDao accountsDao;

    @Inject
    public TransfersService(IAccountsDao accountsDao) {
        this.accountsDao = accountsDao;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccounts() {
        return accountsDao.getAllAccounts();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount() {
        return accountsDao.createAccount();
    }

    @GET
    @Path("/{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam("accountId") int accountId) {
        return accountsDao.getAccount(accountId);
    }

    @PUT
    @Path("/{accountId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putMoney(@PathParam("accountId") int accountId,
                             PutMoneyRequest request) {
        return accountsDao.putMoneyToAccount(accountId, request.getAmount());
    }

    @POST
    @Path("/{accountId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transfer(@PathParam("accountId") int accountId,
                             TransferRequest transferRequest) {
        return accountsDao.createTransfer(accountId, transferRequest.getToAccountId(), transferRequest.getAmount());
    }
}
