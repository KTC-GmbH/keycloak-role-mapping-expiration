# Keycloak Lizenz Server

Diese Projekt ist eine Erweiterung von `Keycloak 18`, um den bestehenden 
OAuth 2.0 Standard zur Realisierung von Lizenzen zu verwenden. 

Kern-Features sind hierbei:
* Verwalten von Kunden
* Verwalten von Anwendungen
* Definition von Lizenzen für eine Anwendung
* Verwalten von Lizenzen eines Kunden

### Domäne
Die bestehenden Modelle in Keycloak werden verwendet, aber umbenannt.
Hierbei sind folgende Änderungen vorgenommen worden:
* **Users -> Kunden**: \
Kunden, welche eine Lizenz besitzen, werden als Keycloak-User repräsentiert. 
* **Clients -> Anwendungen**: \
Eine Anwendung, welche ein Lizenz-Modell besitzen möchte, wird als Keycloak-Client repräsentiert.
* **Roles -> Lizenz-Modell**: \
Ein Lizenz-Modell einer Anwendung wird in Keycloak als Client-Role repräsentiert.

### Verwalten von Gültigkeitszeiträumen einer zugeordneten Lizenz

Im `Lizenz Server` ist es möglich, einer bestimmten Lizenz eines Kunden ein `Gültigkeitsdatum` zu geben,
bis zu welchem dem Kunden die Lizenz zugewiesen sein soll.

Beim Überschreiten dieses Datums wird dem Kunden automatisch die Lizenz entzogen. Hierdurch
ist es also z.B. möglich, Testlizenzen, welche nur einen Monat gültig
sein sollen, zu realisieren.

Falls kein Gültigkeitsdatum gepflegt ist, ist die Lizenz uneingeschränkt dem Kunden
zugeordnet und wird im nicht entzogen.

### Verwalten von Reseller IDs einer zugeordneten Lizenz

Im `Lizenz Server` ist es möglich, einer bestimmten Lizenz eines Kunden eine `Reseller ID`
zuzuordnen, wodurch einer Lizenz die Information hinzugefügt werden kann, durch
wen diese Lizenz verkauft wurde. \
Standardmäßig wird als Reseller ID der Wert "KTC" gesetzt.

### Default-Lizenzen, die jedem Kunden standardmäßig zugeordnet sind

Im `Lizenz Server` ist es möglich, eine Anwendungslizenz standardmäßig allen Kunden
zuzuordnen. Dies kann man durch das Aktivieren des Sliders `Default Lizenz` in der
Detail-Ansicht einer Anwendungslizenz bewerkstelligen:

In der Übersichtsseite der Anwendungslizenzen wird außerdem angezeigt, welche
Lizenzen Default-Lizenzen sind und dadurch allen Kunden zugeordnet werden.

### Default-Gültigkeitsdatum einer Lizenz
Im `Lizenz Server` ist es möglich, einer Anwendungslizenz standardmäßig ein
Gültigkeitsdatum zuzuordnen. Hierdurch ist es möglich, dass einem Kunden
beim erstmaligem Registrieren einer Anwendung für einen bestimmten Zeitraum 
eine erweiterte Lizenz zu geben.

### Automatischen Entfernen von zugeordneten Lizenzen nach Überschreitung des Gültigkeitszeitraums.
Im `Lizenz Server` werden alle Lizenzen, welche einen überschrittenen Gültigkeitszeitraum
besitzen, automatisch entfernt. Dies passiert standardmäßig alle 10min.

### AuthToken Erweiterung um Gültigkeitsdatum und Rollen
Fall eine Anwendung als zusätzlichen Mapper den Mapper Type `KTC :: Lizenz-Eigenschaften in Lizenz`
hinzugefügt hat, wird bei der Abfrage der Lizenz als zusätzliche Information
zu allen aktuell gültigen Lizenz-Modellen pro Anwendung auch die
Gültigkeitsdauer der Lizenz hinzugefügt. \
Hiermit ist es möglich einem Kunden anzuzeigen, ob und wann die Lizenz 
abläuft und das er diese ggf. verlängern muss.