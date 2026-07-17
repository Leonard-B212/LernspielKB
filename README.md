# Lernspiel

Webanwendung für ein Lernspiel im Rahmen eines Projekts an der DHBW.

---

## Voraussetzungen

- Java 23
- Maven 3.9+
- MySQL
- Git
- Visual Studio Code (empfohlen)

---

## Repository klonen

```bash
git clone <https://github.com/Leonard-B212/LernspielKB>
cd lernspiel
```

---

## Projekt starten

Zunächst das gesamte Multi-Module-Projekt bauen:

```bash
mvn clean install
```

Anschließend das Spring-Boot-Modul starten:

```bash
cd lernspiel-app
mvn spring-boot:run
```

Die Anwendung ist anschließend unter:

```
http://localhost:8080
```

erreichbar.

---

## API-Endpunkte

> Die wichtigsten Endpunkte (werden noch ergänzt)

### Debug-Endpunkte

Die folgenden Endpunkte dienen ausschließlich der lokalen Entwicklung und zum Testen der Benutzerverwaltung.

| Methode  | Endpunkt                                       | Beschreibung                                                                                                 |
| -------- | ---------------------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| `GET`    | `/debug/db-info`                               | Zeigt Informationen über die aktuell verbundene Datenbank (Datenbankname, Benutzer, Host, Port und Version). |
| `GET`    | `/debug/create-test-user`                      | Erstellt einen Testbenutzer in der Datenbank.                                                                |
| `GET`    | `/debug/list-users`                            | Gibt eine Liste aller gespeicherten Benutzer zurück.                                                         |
| `DELETE` | `/debug/drop-user`                             | Löscht die Tabelle `user` aus der Datenbank. **Nur für Entwicklungszwecke verwenden!**                       |
| `GET`    | `/debug/login?userID={id}&password={passwort}` | Testet den Login eines Benutzers und gibt bei Erfolg einen JWT zurück.                                       |


---

## Projektstruktur

```text
lernspiel/
│
├── auth-service/      # Authentifizierung und Benutzerverwaltung
├── common/            # Gemeinsame DTOs, Modelle und Hilfsklassen
├── game-service/      # Spiellogik und spielbezogene Funktionen
├── lernspiel-app/     # Spring-Boot-Hauptanwendung
│
├── .gitignore         # Von Git ignorierte Dateien und Ordner
├── README.md          # Projektdokumentation
└── pom.xml            # Parent-POM und Maven-Modulverwaltung
```