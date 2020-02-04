# Transfers as backend test task

## Used stack:
1. Jersey - rest framework
2. Grizzly2 - http server
3. hk2 - ioc container
4. maven - build tool
5. junit4 - tests
6. mockito - mocking objects in tests
7. lombok - code writing shortener
8. h2 - database
9. hibernate - orm


## API:

Base Uri: http://localhost:8080/api/

| method | endpoint | request | response | description|
|:------:|:---------|:--------|:---------|:-----------|
| GET    |/accounts |         |[{"accountId": 1, "balance": 0.00},<br/> {"accountId": 2, "balance": 0.00}] |get all created accounts|
| POST   |/accounts |         |{"accountId": 1, "balance": 0.00} | create new account|
| GET    |/accounts/{account_id}||{"accountId": 1, "balance": 0.00}|get account by id|
| PUT    |/accounts/{account_id}|{"amount": 100}|{"accountId": 1, "balance": 100.00}|add money to account|
| POST   |/accounts/{account_id}/transfer|{"toAccount":1, "amount":100}|{"amount":100,"from":{"accountId":1,"balance":71.00},"to":{"accountId":2,"balance":152.00}}|make transfer between accounts|

## Try it

`mvn clean package ` will create fat jar

`java -jar transfers-1.0-SNAPSHOT-jar-with-dependencies.jar` will start http server
