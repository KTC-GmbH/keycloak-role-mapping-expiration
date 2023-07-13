# Paketiert die Erweiterungen als Deployable JAR.
build:
	mvn clean package

# Startet eine neue Keycloak-Instanz mit der lokalen Version
# der Erweiterungen als Docker-Container.
serve: build
	docker compose down && \
    docker build . -t keycloak-lizenz-server && \
    docker compose up

test:
	./test.sh

# Stoppt die Keycloak-Instanz und räumt zuvor gebaute Versionen
# der Erweiterung auf.
clean:
	docker compose down && \
	mvn clean