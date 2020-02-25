# Simple transfer app
Small transfer app without Spring framework

## Requirements:
  * Java JDK 11
  * Maven 3.6
## To run:
  * mvn install
  * java -jar target/transfer-1.0-jar-with-dependencies.jar
  
## REST Api:
  * Accounts
    * GET localhost:8080/account/{id}
    * POST localhost:8080/account 
    `{
        "name": "b",
        "balance": 50
    }`
  * Transfer
    * POST localhost:8080/transfer 
    `{ 
        "amount": 20,
        "to": {
            "id": 1
        },
        "from": {
            "id": 2
        }
    }`
