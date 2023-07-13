# Anforderungen
* Java 11
* Maven 3.9.2+
* Docker 20.10.12+

# Paketieren
Ein neues Artefakt kann mit folgendem Befehl erstellt werden:
```
mvn clean package 
```
Nach der Ausführung kann das erstellte Artefakt unter folgendem Pfad gefunden werden: `./target/keycloak-user-role-mapping-expiration.jar`

# Testen
Um alle Tests für dieses Projekt auszuführen, kann folgender Befehl ausgeführt werden:
```
mvn clean test
```

Um lokal eine Testumgebung zu starten, kann folgender Befehl ausgeführt werden: 
```
make serve
```

Hiermit werden zwei Docker Container gestartet:
- Ein Container für Keycloak, auf dem die entwickelten Erweiterungen deployed sind.
- Ein Container für PostgreSQL.

Die genaue Konfiguration der Container kann in der `docker-compose.yml` nachgelesen werden.

Um die lokale Testumgebung zu stoppen, kann folgender Befehl ausgeführt werden: 
```
make clean
```