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

### Ausführungsrichtlinie (nur einmal notwendig)

Damit das PowerShell-Skript ausgeführt werden kann, muss die Ausführungsrichtlinie einmalig angepasst werden:

```powershell
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
```

Danach kann das Skript in allen neuen PowerShell-Terminals ohne weitere Einstellungen ausgeführt werden.

> **Hinweis:** Diese Einstellung muss nur einmal durchgeführt werden. Mit `RemoteSigned` dürfen lokal erstellte PowerShell-Skripte ausgeführt werden, während aus dem Internet heruntergeladene Skripte weiterhin besonderen Sicherheitsprüfungen unterliegen.

---

### Entwicklungs-Skript (empfohlen)

Im Projekt befindet sich das Skript `dev.ps1`, das die häufigsten Entwicklungsaufgaben vereinfacht.

Starten:

```powershell
.\dev.ps1
```

Anschließend erscheint ein Menü:

```text
==========================================
          Lernspiel Entwicklung
==========================================

[1] Clean Install
[2] Clean Install und Start
[3] Nur Start
[0] Beenden
```

Das erlaubt es einfach einen Clean Install / Start durchzuführen ohne permanent in der CLI das Directory zu wechseln.

### Manueller Start

Alternativ kann das Projekt auch manuell gestartet werden.

---
Zunächst das gesamte Multi-Module-Projekt bauen:

```bash
mvn clean install
```

Anschließend das Spring-Boot-Modul starten:

```bash
cd lernspiel-app
mvn spring-boot:run
```

Die Anwendung ist anschließend unter dieser URL erreichbar:

```
http://localhost:8080
```


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
├── dev.ps1            # Entwicklungs-Skript zum Bauen und Starten
├── README.md          # Projektdokumentation
└── pom.xml            # Parent-POM und Maven-Modulverwaltung
```