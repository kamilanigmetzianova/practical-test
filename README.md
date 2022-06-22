
# Backend Developer Practical Test

> RESTful API for bank accounts system

The system has moduled structure, including api, service and data layers.

### Technologies
- Written in Kotlin + Spring Boot
- Relational data storing 
- Swagger for API documentation 
- H2 in-memory database for backing store (console http://localhost:8080/h2-console)
- Inputs were validated using Spring validation module. 
- Service layer is covered with unit tests.
- For mocking purposes, decided to use Mockk instead of Mockito, because Mockk is better suited for Kotlin.  

### Database:

**beneficiaries**  
id  
name

**accounts**  
id  
account_number  
beneficiary  
pin_code
balance  
created_at

**transactions**  
id  
account   
type  
balanceBefore  
balanceAfter  
created_at

### Operations:  

- GET **/api/account/fetch-all** - get all accounts
- GET **/api/account/{account-id}/transactions** - get all transaction's history for specific account (history is kept only for succeeded transactions)
- POST **/api/account/create** - create account
- POST **/api/transaction/deposit** - deposit money into account
- POST **/api/transaction/withdraw** - withdraw money 
- POST **/api/transaction/transfer** - transfer money from one account to another (could be replaced by combination of deposit and withdraw operations, but decided to go a separate way for ability of further expansion)

Visit Swagger for more detailed API description (link http://localhost:8080/swagger-ui.html). You can interact with API using Swagger HTTP requests.  
Data storage filled with initial beneficiaries, accounts and transactions. Default PinCode is **1111**.


