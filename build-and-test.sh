#!/bin/bash

echo -e "\033[0m --- \033[32mCleanup old test deployment\033[0m @ \033[34mKeycloak - User Role Mapping Expiration \033[0m---"
docker compose down

echo ""
echo -e "\033[0m --- \033[32mBuild new artifact\033[0m @ \033[34mKeycloak - User Role Mapping Expiration \033[0m---"
mvn clean package -B

echo ""
echo -e "\033[0m --- \033[32mDeploy artifact for testing\033[0m @ \033[34mKeycloak - User Role Mapping Expiration \033[0m---"
docker compose up -d --build