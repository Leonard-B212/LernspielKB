# Lernspiel

Webanwendung für ein Lernspiel im Rahmen eines Projekts an der DHBW.

---

## Voraussetzungen

- Java 23
- Maven 3.9+
- MySQL
- Git
- Visual Studio Code (wird empfohlen)

---

## Repository klonen

```bash
git clone https://github.com/Leonard-B212/LernspielKB
cd LernspielKB
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

```text
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
| ----------- | -------- |
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

## Frontend

Das Frontend wurde mit **HTML, CSS und Vanilla JavaScript** umgesetzt.

Neben der Benutzerverwaltung enthält das Projekt eine Code-Sandbox, mit der Programme visuell aus Code-Blöcken zusammengesetzt und anschließend an den Interpreter übergeben werden können.

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
                ├── sandbox.html
                ├── css/
                │   └── style.css
                └── js/
                    ├── api.js
                    ├── login.js
                    ├── admin.js
                    ├── teacher.js
                    ├── student.js
                    └── sandbox.js
```

---

## Code-Sandbox

Die Code-Sandbox dient als Grundlage für das eigentliche Lernspiel.

Code wird dabei nicht direkt als Text eingegeben. Stattdessen können die Schülerinnen und Schüler einzelne Code-Blöcke per **Drag & Drop** zu einem Programm zusammensetzen.

Aktuell stehen unter anderem folgende Blöcke zur Verfügung:

### Datentypen

- `int`
- `String`
- `boolean`

### Weitere Code-Blöcke

- Variablen
- Werte
- `=`
- `+`
- `-`
- `*`
- `/`
- `;`

Die Sandbox unterstützt mehrere Codezeilen. Blöcke können innerhalb des Programms verschoben, zwischen vorhandenen Blöcken eingefügt und über eine Drop-Zone wieder gelöscht werden.

Über den Button **Ausführen** wird das erstellte Programm in ein `ProgramRequest`-Objekt überführt und als JSON an das Backend gesendet.

Die Antwort des Interpreters wird anschließend direkt in einer Interpreter-Konsole im Frontend dargestellt.

Für die Interpreter-Konsole stehen aktuell drei Darstellungsmodi zur Verfügung:

- Dark
- Kernel
- Light

Die Sandbox ist aktuell direkt erreichbar unter:

```text
http://localhost:8080/sandbox.html
```

---

## Interpreter

Der Interpreter befindet sich im Modul `game-service`.

Das Frontend erstellt aus den verwendeten Code-Blöcken eine Liste von `CodeBlock`-Objekten. Zusammen mit weiteren Informationen wird diese als `ProgramRequest` an den Interpreter gesendet.

Vereinfacht läuft die Verarbeitung folgendermaßen ab:

```text
Code-Sandbox
    ↓
ProgramRequest (JSON)
    ↓
InterpreterController
    ↓
InterpreterService
    ↓
Ausführung des Programms
    ↓
ExecutionLog
    ↓
List<String>
    ↓
Interpreter-Konsole im Frontend
```

Für unterschiedliche Arten von Code-Blöcken existieren spezialisierte DTOs, beispielsweise:

- `CodeBlock`
- `VarNameBlock`
- `ValueBlock`
- `IfStatementBlock`
- `ElseStatementBlock`

Die polymorphe Deserialisierung der Code-Blöcke erfolgt über Jackson.

Die während der Ausführung erzeugten Meldungen werden in einem `ExecutionLog` gesammelt und anschließend als `List<String>` an das Frontend zurückgegeben.

Dadurch kann die Sandbox sowohl erfolgreiche Verarbeitungsschritte als auch vom Interpreter erkannte Fehler direkt anzeigen.

---

## API-Endpunkte

### Authentifizierung

| Methode | Endpunkt | Beschreibung | Berechtigung |
| ------- | -------- | ------------ | ------------ |
| `POST` | `/api/benutzer/login` | Benutzer anmelden und JWT erhalten | Öffentlich |
| `GET` | `/api/benutzer/me` | Aktuell angemeldeten Benutzer abrufen | Authentifiziert |

---

### Benutzerverwaltung

| Methode | Endpunkt | Beschreibung | Berechtigung |
| ------- | -------- | ------------ | ------------ |
| `POST` | `/api/benutzer/register/teacher` | Lehrer anlegen | Admin |
| `POST` | `/api/benutzer/register/student` | Schüler anlegen | Admin, Teacher |
| `GET` | `/api/benutzer` | Alle Benutzer anzeigen | Admin |
| `DELETE` | `/api/benutzer/{userID}` | Benutzer löschen | Admin |
| `GET` | `/api/benutzer/me/students` | Eigene Schüler eines Lehrers abrufen | Teacher |

---

### Klassenverwaltung

| Methode | Endpunkt | Beschreibung | Berechtigung |
| ------- | -------- | ------------ | ------------ |
| `POST` | `/api/classes` | Neue Klasse anlegen | Admin, Teacher |
| `GET` | `/api/classes` | Alle Klassen abrufen | Authentifiziert |
| `GET` | `/api/classes/{id}` | Einzelne Klasse abrufen | Authentifiziert |

---

### Interpreter

| Methode | Endpunkt | Beschreibung |
| ------- | -------- | ------------ |
| `POST` | `/game/interpreter/run` | Führt ein aus Code-Blöcken bestehendes Programm über den Interpreter aus |

Der Endpunkt erwartet einen `ProgramRequest` im Request-Body.

Beispielhafte Struktur:

```json
{
  "userId": 1,
  "levelId": 1,
  "languageId": 1,
  "program": [
    {
      "type": "INT"
    },
    {
      "type": "VAR_NAME",
      "name": "x"
    },
    {
      "type": "EQUALS"
    },
    {
      "type": "VALUE",
      "value": {
        "value": 8,
        "type": "INT"
      }
    },
    {
      "type": "BREAK"
    }
  ]
}
```

Die Antwort besteht aktuell aus einer Liste von Meldungen des Interpreters.

Beispiel:

```json
[
  "variableDeclaration gestartet mit 5 Blöcken",
  "Variable x INT mit Initialwert deklariert: 8"
]
```

---

### Debug-Endpunkte

Die folgenden Endpunkte dienen ausschließlich der lokalen Entwicklung und zum Testen der Benutzerverwaltung.

| Methode | Endpunkt | Beschreibung |
| ------- | -------- | ------------ |
| `GET` | `/debug/db-info` | Zeigt Informationen über die aktuell verbundene Datenbank (Datenbankname, Benutzer, Host, Port und Version). |
| `GET` | `/debug/create-test-user` | Erstellt einen Testbenutzer in der Datenbank. |
| `GET` | `/debug/list-users` | Gibt eine Liste aller gespeicherten Benutzer zurück. |
| `DELETE` | `/debug/drop-user` | Löscht die Tabelle `user`. **Nur für Entwicklungszwecke verwenden!** |
| `GET` | `/debug/login?userID={id}&password={passwort}` | Testet den Login eines Benutzers und gibt bei Erfolg ein JWT zurück. |

---

## Aktueller Entwicklungsstand

Die grundlegende Architektur des Projekts ist vorhanden.

Aktuell umgesetzt sind unter anderem:

- Authentifizierung über JWT
- Benutzerverwaltung
- Rollen für Administratoren, Lehrer und Schüler
- Klassenverwaltung
- Multi-Module-Struktur mit Maven
- `game-service` für die Spiellogik
- Interpreter für die Verarbeitung von Code-Blöcken
- Polymorphe CodeBlock-DTOs
- Code-Sandbox mit Drag & Drop
- Mehrzeilige Programme
- Verschieben und Löschen von Code-Blöcken
- Übertragung der Programme an das Backend
- Ausgabe des Interpreters im Frontend
- Darstellung von Interpreterfehlern
- Mehrere Darstellungsmodi für die Interpreter-Konsole

Die Code-Sandbox dient aktuell als Grundlage und Demonstration der späteren Level.

Im weiteren Projektverlauf sollen Level dynamisch bereitgestellt werden. Dabei sollen Schülerinnen und Schüler vorgegebene Aufgaben lösen, indem sie die verfügbaren Code-Blöcke korrekt zusammensetzen. Die erstellten Programme können anschließend vom Interpreter ausgewertet werden.

---

## Projektstruktur

```text
lernspiel/
│
├── auth-service/       # Authentifizierung und Benutzerverwaltung
│
├── common/             # Gemeinsame DTOs, Modelle und Hilfsklassen
│
├── game-service/       # Interpreter, Spiellogik und spielbezogene DTOs
│
├── lernspiel-app/      # Spring-Boot-Hauptanwendung und Frontend
│   └── src/main/resources/static/
│       ├── index.html
│       ├── admin.html
│       ├── teacher.html
│       ├── student.html
│       ├── sandbox.html
│       ├── css/
│       │   └── style.css
│       └── js/
│           ├── api.js
│           ├── login.js
│           ├── admin.js
│           ├── teacher.js
│           ├── student.js
│           └── sandbox.js
│
├── .gitignore          # Von Git ignorierte Dateien und Ordner
├── dev.ps1             # Entwicklungs-Skript zum Bauen und Starten
├── README.md           # Projektdokumentation
└── pom.xml             # Parent-POM und Maven-Modulverwaltung
```