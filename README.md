# Application allows to create account, put money on it, withdraw money and transfer between accounts
## Libraries and frameworks
* Java 11
* Spark 2.8.0
* Lombok 1.18.6
* Logback 1.2.3
* H2 Database 1.4.199
* JUnit 4.12
* Mockito 2.27.0
* Hamcrest 1.3
* Apache HTTP Client 4.5.8
* jOOQ 3.11.11
* Google Gson 2.8.0
* Google Guice 4.2.2
* Hikari Connection Pool 3.3.1

## Requirements
* Apache Maven 3.5.4
* Environment variable JAVA_HOME is configured for Java 11 directory

## How to build
* Navigate to <project_directory>
* Run _mvn clean install_

## How to run
* Navigate to <project_directory>/target
* Run _java -jar money-1.0-SNAPSHOT-jar-with-dependencies.jar_

## How to use
### Create account
_curl -d "" http://localhost:4567/accounts_

### Get account information
_curl http://localhost:4567/accounts/1_

### Put money
_curl -d '{"accountId": 1, "sum": 1000}' http://localhost:4567/accounts/put_

### Withdraw money
_curl -d '{"accountId": 1, "sum": 1000}' http://localhost:4567/accounts/withdraw_

### Transfer money
_curl -d '{"fromAccountId": 1, "toAccountId": 2, "sum": 100}' http://localhost:4567/accounts/transfer_