# WALLET PROJECT

It is a project which simulates a digital wallet running on the JVM.

## Restrictions

The balance in wallet can be modified by transactions,
transaction will be only executed if wallet is enabled ,
in withdraw transactions ballance should be more than withdraw amount.
For transfering money between two wallets an escrow acount is used,the card number of this account can be set in application.properties
For transfering money between two wallets of the same user,both of the wallets should exist
For transfering money between two wallets of the different users,the other user and wallets should exist

## Project Description

this application was implemented
with Java11, Spring Boot and Maven. You can find the detail information regarding this project such as requirements, running procedure, 
testing procedure, api endpoints, out of scope and scalable system scope and JWT for security. 

##Technologies
1. Java11
2. Spring Boot
3. Spring Data JPA
4. MySQL
5. JWT
6. slf4j
7. Maven
8. JUnit
9. H2

## Requirements and steps to run this application
1. Install Java 11
2. Maven to build the application. 
3. Download and install MySQL server
4. Connect to the MySQL server
5. Make mysql configurations in application.properties like below

```
spring.datasource.url = jdbc:mysql://localhost:3306/db_wallett?createDatabaseIfNotExist=true 
spring.datasource.username = <your MySQL user>
spring.datasource.password = <your MySQL password>
```

## Running

If you want to run this application you need to follow the "Requirements and steps to run this application" part.
You should run the "WalletApplication" class and this application is running on default port 8080. You can change this
port from application.properties


## Testing

There are unit tests regarding the wallet application. I implemented unit test
for most of functionalities of services. For running tests I configured H2 which is set in
application.properties in resources in test folder.


## Endpoints
You can find endpoints,required parameters and detail information at postman collection that was atteched to email.
