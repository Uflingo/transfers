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


## Methods:
1. GET /accounts - get all created accounts
2. POST /accounts - creates new account
3. GET /accounts/{account_id} - get account by id
4. PUT /accounts/{account_id}, request: {"amount": 100} - adds money to account
5. POST /accounts/{account_id}/transfers, request: {"toAccount":1, "amount":100} - make transfer between accounts
