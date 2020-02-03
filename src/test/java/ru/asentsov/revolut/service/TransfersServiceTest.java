package ru.asentsov.revolut.service;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.asentsov.revolut.Application;
import ru.asentsov.revolut.model.PutMoneyRequest;
import ru.asentsov.revolut.model.TransferRequest;
import ru.asentsov.revolut.model.TransferResponse;
import ru.asentsov.revolut.model.view.AccountView;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;

public class TransfersServiceTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() {
        server = Application.startServer();
        Client c = ClientBuilder.newClient();
        target = c.target(Application.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdownNow();
    }

    @Test
    public void createAccountTest() {
        Response response = target.path("accounts").request(MediaType.APPLICATION_JSON).post(null);

        assertEquals(CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void getAccountTest() {
        Response response = target.path("accounts").request(MediaType.APPLICATION_JSON).post(null);

        assertEquals(CREATED.getStatusCode(), response.getStatus());

        AccountView createdAccount = response.readEntity(AccountView.class);
        Response getAccountResponse = target.path("accounts/" + createdAccount.getAccountId()).request().get();
        assertEquals(OK.getStatusCode(), getAccountResponse.getStatus());
        assertEquals(createdAccount, getAccountResponse.readEntity(AccountView.class));
    }

    @Test
    public void addMoneyTest() {
        BigDecimal amount = new BigDecimal("15.00");

        Response createAccountResponse = target.path("accounts").request(MediaType.APPLICATION_JSON).post(null);
        assertEquals(CREATED.getStatusCode(), createAccountResponse.getStatus());
        AccountView accountView = createAccountResponse.readEntity(AccountView.class);
        Response putResponse = target.path("accounts/" + accountView.getAccountId())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(new PutMoneyRequest(amount), MediaType.APPLICATION_JSON));

        assertEquals(CREATED.getStatusCode(), putResponse.getStatus());
        assertEquals(new AccountView(accountView.getAccountId(), amount), putResponse.readEntity(AccountView.class));
    }

    @Test
    public void transferTest() {
        BigDecimal initBalance = new BigDecimal("10.00");
        BigDecimal transferAmount = new BigDecimal("3.41");

        Response response1 = target.path("accounts").request(MediaType.APPLICATION_JSON).post(null);
        assertEquals(CREATED.getStatusCode(), response1.getStatus());
        AccountView account1 = response1.readEntity(AccountView.class);

        Response response2 = target.path("accounts").request(MediaType.APPLICATION_JSON).post(null);
        assertEquals(CREATED.getStatusCode(), response2.getStatus());
        AccountView account2 = response2.readEntity(AccountView.class);

        Response putResponse = target.path("accounts/" + account1.getAccountId())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(new PutMoneyRequest(initBalance), MediaType.APPLICATION_JSON));

        assertEquals(CREATED.getStatusCode(), putResponse.getStatus());
        account1 = putResponse.readEntity(AccountView.class);

        Response transferResult = target.path("accounts/" + account1.getAccountId())
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(new TransferRequest(account2.getAccountId(), transferAmount), MediaType.APPLICATION_JSON));

        assertEquals(CREATED.getStatusCode(), transferResult.getStatus());

        TransferResponse transferResponse = transferResult.readEntity(TransferResponse.class);
        assertEquals(initBalance.subtract(transferAmount), transferResponse.getFrom().getBalance());
        assertEquals(transferAmount, transferResponse.getTo().getBalance());
    }
}
