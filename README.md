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

## Datenbank

Für das Projekt wird **keine SQL-Datei** benötigt.

Die Datenbankstruktur wird beim Start automatisch von Hibernate anhand der vorhandenen Entity-Klassen erstellt (`spring.jpa.hibernate.ddl-auto=update`).

---

## Bootstrap-Administrator

Beim ersten Start der Anwendung wird automatisch ein Administrator angelegt, sofern sich noch kein Administrator in der Datenbank befindet.

**Standard-Zugangsdaten:**

| Benutzer-ID | Passwort |
|-------------|-----------|
| `1` | `admin123` |

> **Hinweis:** Wird die Datenbank gelöscht, wird beim nächsten Start automatisch wieder ein Administrator mit der Benutzer-ID `1` erstellt.

---

## Benutzerrollen

Das Projekt besitzt aktuell drei Benutzerrollen:

### Administrator

- Lehrer anlegen
- Schüler anlegen
- Klassen anlegen
- Alle Benutzer anzeigen
- Benutzer löschen

### Lehrer

- Eigene Klassen anlegen
- Schüler für eigene Klassen anlegen
- Eigene Schüler anzeigen

### Schüler

- Eigenes Benutzerprofil anzeigen

---

## Dummy-Frontend

Aktuell enthält das Projekt ein einfaches Dummy-Frontend zur Demonstration der Benutzerverwaltung.

Dieses wurde bewusst mit **HTML, CSS und Vanilla JavaScript** umgesetzt und dient ausschließlich der Entwicklung.

Für den finalen Stand des Projekts ist geplant, dieses Frontend durch eine React-Anwendung zu ersetzen.

Das Frontend befindet sich unter:

```text
lernspiel-app/
└── src/
    └── main/
        └── resources/
            └── static/
                ├── index.html
                ├── admin.html
                ├── teacher.html
                ├── student.html
                ├── css/
                └── js/
```

---

## API-Endpunkte

### Authentifizierung

| Methode | Endpunkt | Beschreibung | Berechtigung |
|----------|----------|--------------|--------------|
| `POST` | `/api/benutzer/login` | Benutzer anmelden und JWT erhalten | Öffentlich |
| `GET` | `/api/benutzer/me` | Aktuell angemeldeten Benutzer abrufen | Authentifiziert |

---

### Benutzerverwaltung

| Methode | Endpunkt | Beschreibung | Berechtigung |
|----------|----------|--------------|--------------|
| `POST` | `/api/benutzer/register/teacher` | Lehrer anlegen | Admin |
| `POST` | `/api/benutzer/register/student` | Schüler anlegen | Admin, Teacher |
| `GET` | `/api/benutzer` | Alle Benutzer anzeigen | Admin |
| `DELETE` | `/api/benutzer/{userID}` | Benutzer löschen | Admin |
| `GET` | `/api/benutzer/me/students` | Eigene Schüler eines Lehrers abrufen | Teacher |

---

### Klassenverwaltung

| Methode | Endpunkt | Beschreibung | Berechtigung |
|----------|----------|--------------|--------------|
| `POST` | `/api/classes` | Neue Klasse anlegen | Admin, Teacher |
| `GET` | `/api/classes` | Alle Klassen abrufen | Authentifiziert |
| `GET` | `/api/classes/{id}` | Einzelne Klasse abrufen | Authentifiziert |

---

### Debug-Endpunkte

Die folgenden Endpunkte dienen ausschließlich der lokalen Entwicklung und zum Testen der Benutzerverwaltung.

| Methode | Endpunkt | Beschreibung |
|----------|----------|--------------|
| `GET` | `/debug/db-info` | Zeigt Informationen über die aktuell verbundene Datenbank (Datenbankname, Benutzer, Host, Port und Version). |
| `GET` | `/debug/create-test-user` | Erstellt einen Testbenutzer in der Datenbank. |
| `GET` | `/debug/list-users` | Gibt eine Liste aller gespeicherten Benutzer zurück. |
| `DELETE` | `/debug/drop-user` | Löscht die Tabelle `user`. **Nur für Entwicklungszwecke verwenden!** |
| `GET` | `/debug/login?userID={id}&password={passwort}` | Testet den Login eines Benutzers und gibt bei Erfolg ein JWT zurück. |

---

## Projektstruktur

```text
lernspiel/
│
├── auth-service/       # Authentifizierung und Benutzerverwaltung
├── common/             # Gemeinsame DTOs, Modelle und Hilfsklassen
├── game-service/       # Spiellogik und spielbezogene Funktionen
├── lernspiel-app/      # Spring-Boot-Hauptanwendung
│   └── src/main/resources/static/
│       ├── index.html
│       ├── admin.html
│       ├── teacher.html
│       ├── student.html
│       ├── css/
│       └── js/
│
├── .gitignore          # Von Git ignorierte Dateien und Ordner
├── dev.ps1             # Entwicklungs-Skript zum Bauen und Starten
├── README.md           # Projektdokumentation
└── pom.xml             # Parent-POM und Maven-Modulverwaltung
```