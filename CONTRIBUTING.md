# Requirements
* Java 11
* Maven 3.8.1+

# Building the code
An artifact can be build by running the command:
```
mvn clean package 
```
After executing the command the final artifact can be found at `./target/keycloak-user-role-mapping-expiration.jar`

# Testing 
To execute all tests you have to run the following command:
```
mvn clean test
```

A test environment can be started by running the script `./build-and-test.sh`.
This script will start two docker containers:
- A container for keycloak with the deployed extension
- A container for postgresql

The detailed configuration for both containers can be found in the `docker-compose.yml` file.