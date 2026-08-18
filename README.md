# Lernspiel

Webbasiertes Lernspiel für Schülerinnen und Schüler der Klassenstufen 9–10 zum spielerischen Erlernen grundlegender Programmierkonzepte.

Das Projekt entsteht im Rahmen eines Projekts an der DHBW und kombiniert einen visuellen Drag-&-Drop-Code-Editor mit einem eigenen Interpreter.

---

## Inhalt

- [Überblick](#überblick)
- [Schnellstart](#schnellstart)
  - [Voraussetzungen](#voraussetzungen)
  - [Repository klonen](#repository-klonen)
  - [Projekt starten](#projekt-starten)
  - [Test-Zugangsdaten](#test-zugangsdaten)
- [Bedienung und Testen](#bedienung-und-testen)
  - [Benutzerrollen](#benutzerrollen)
  - [Code-Sandbox](#code-sandbox)
  - [Dynamische Level](#dynamische-level)
- [Architektur](#architektur)
  - [Frontend](#frontend)
  - [Interpreter](#interpreter)
  - [Level-Service](#level-service)
  - [Datenbank](#datenbank)
- [Projektstruktur](#projektstruktur)
- [API-Endpunkte](#api-endpunkte)
- [Entwicklung](#entwicklung)
  - [Manueller Start](#manueller-start)
  - [Aktueller Entwicklungsstand](#aktueller-entwicklungsstand)

---

# Überblick

Das Lernspiel soll Schülerinnen und Schülern grundlegende Programmierkonzepte vermitteln.

Programme werden dabei nicht direkt als Quelltext eingegeben. Stattdessen können Code-Blöcke per Drag & Drop zu einem Programm zusammengesetzt werden. Das erstellte Programm wird anschließend an einen eigenen Interpreter übergeben und ausgewertet.

Aktuell umgesetzt sind unter anderem:

- Authentifizierung über JWT
- Benutzerverwaltung
- Rollen für Administratoren, Lehrer und Schüler
- Klassenverwaltung
- visueller Drag-&-Drop-Code-Editor
- mehrzeilige Programme
- Verschieben und Löschen von Code-Blöcken
- eigener Interpreter
- Übertragung der Programme an das Backend
- Interpreter-Ausgabe direkt im Frontend
- Darstellung von Interpreterfehlern
- verschiedene Darstellungsmodi der Interpreter-Konsole
- Datenbankmodell für dynamische Level
- Verwaltung von Leveln und verfügbaren Code-Komponenten
- REST-Schnittstelle zum Anlegen und Laden von Leveln
- dynamisches Laden von Leveln im Frontend
- dynamische Bereitstellung der für ein Level verfügbaren Code-Blöcke

Die Code-Sandbox dient weiterhin als frei nutzbare technische Umgebung für den visuellen Editor und Interpreter.

Zusätzlich können Level inzwischen dynamisch aus der Datenbank geladen werden. Ein Level definiert unter anderem Aufgabenstellung, Kategorie, Programmiersprache und die dafür verfügbaren Code-Blöcke.

---

# Schnellstart

## Voraussetzungen

Für die lokale Ausführung werden folgende Komponenten benötigt:

- Java 23
- Maven 3.9+
- MySQL
- Git
- Visual Studio Code (empfohlen)

## Repository klonen

```bash
git clone https://github.com/Leonard-B212/LernspielKB
cd LernspielKB
```

## Projekt starten

Für die Entwicklung steht das PowerShell-Skript `dev.ps1` zur Verfügung.

### PowerShell-Ausführungsrichtlinie

Falls PowerShell die Ausführung des Skripts verhindert, muss einmalig folgende Ausführungsrichtlinie gesetzt werden:

```powershell
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
```

> **Hinweis:** Diese Einstellung muss nur einmal durchgeführt werden. Sie gilt ausschließlich für den aktuellen Windows-Benutzer. Mit `RemoteSigned` dürfen lokal erstellte PowerShell-Skripte ausgeführt werden, während aus dem Internet heruntergeladene Skripte weiterhin besonderen Sicherheitsprüfungen unterliegen.

Danach kann das Projekt gestartet werden mit:

```powershell
.\dev.ps1
```

Im Menü anschließend:

```text
==========================================
          Lernspiel Entwicklung
==========================================

[1] Clean Install
[2] Clean Install und Start
[3] Nur Start
[0] Beenden
```

Für einen vollständigen ersten Start empfiehlt sich:

```text
[2] Clean Install und Start
```

Die Anwendung ist anschließend erreichbar unter:

```text
http://localhost:8080
```

## Test-Zugangsdaten

Beim ersten Start wird automatisch ein Administrator angelegt, sofern noch kein Administrator in der Datenbank vorhanden ist.

| Benutzer-ID | Passwort |
| ----------- | -------- |
| `1` | `admin123` |

> **Hinweis:** Wird die Datenbank gelöscht, wird beim nächsten Start automatisch wieder ein Administrator mit der Benutzer-ID `1` erstellt.

---

# Bedienung und Testen

## Benutzerrollen

Das Projekt besitzt aktuell drei Benutzerrollen.

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

## Code-Sandbox

Die Code-Sandbox dient als frei nutzbare technische Umgebung für den visuellen Code-Editor und den Interpreter.

Code wird nicht direkt als Text eingegeben. Stattdessen können die Schülerinnen und Schüler einzelne Code-Blöcke per **Drag & Drop** zu einem Programm zusammensetzen.

Aktuell stehen unter anderem folgende Blöcke zur Verfügung:

**Datentypen**

- `int`
- `String`
- `boolean`

**Weitere Code-Blöcke**

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

## Dynamische Level

Neben der frei nutzbaren Sandbox können Level dynamisch aus der Datenbank geladen werden.

Ein Level enthält aktuell unter anderem:

- einen Namen
- eine Beschreibung bzw. Aufgabenstellung
- eine Kategorie
- eine Levelnummer
- eine Programmiersprache
- die für das Level verfügbaren Code-Blöcke
- die konfigurierte Anzahl der jeweiligen Code-Blöcke

Die Kombination aus Kategorie, Levelnummer und Programmiersprache identifiziert ein Level eindeutig.

Die Level-Seite verwendet dieselben wiederverwendbaren Editor-Komponenten wie die Sandbox. Die Block-Palette wird jedoch nicht statisch im HTML definiert, sondern anhand der Daten des geladenen Levels erzeugt.

Ein Level kann aktuell über seine ID geladen werden:

```text
http://localhost:8080/level.html?id=1
```

Die Level-Seite lädt anschließend die zugehörigen Daten über den Level-Service und zeigt unter anderem Aufgabenstellung, Kategorie, Programmiersprache und verfügbare Code-Blöcke an.

Die in der Datenbank gespeicherte Anzahl der verfügbaren Komponenten wird bereits an das Frontend übertragen. Eine tatsächliche Begrenzung der maximal verwendbaren Blockanzahl im Editor ist aktuell noch nicht umgesetzt.

---

# Architektur

Das Projekt ist als Maven-Multi-Module-Anwendung aufgebaut.

Die einzelnen Module trennen Authentifizierung, gemeinsam verwendete Komponenten, Spiellogik, Levelverwaltung und die ausführbare Spring-Boot-Anwendung logisch voneinander.

## Frontend

Das Frontend wurde mit **HTML, CSS und Vanilla JavaScript** umgesetzt.

Neben der Benutzerverwaltung enthält es den visuellen Code-Editor der Sandbox sowie eine Level-Seite für dynamisch geladene Aufgaben.

Die JavaScript-Komponenten des Editors sind modular aufgebaut. Zentrale Funktionen wie Blockdefinitionen, Drag & Drop, Rendering und Editor-State werden dadurch sowohl von der Sandbox als auch von der Level-Seite verwendet.

Die Sandbox definiert ihre verfügbaren Code-Blöcke statisch und dient als freie Testumgebung.

Die Level-Seite lädt dagegen die Leveldaten über den Level-Service. Anhand der zurückgegebenen Components wird die Block-Palette dynamisch erzeugt.

Vereinfacht läuft das Laden eines Levels folgendermaßen ab:

```text
level.html?id=1
    ↓
level.js
    ↓
levelApi.js
    ↓
GET /api/levels/1
    ↓
LevelController
    ↓
LevelService
    ↓
Datenbank
    ↓
LevelResponse
    ↓
dynamische Level-Informationen und Block-Palette
```

Die detaillierte Verzeichnisstruktur ist unter [Projektstruktur](#projektstruktur) aufgeführt.

## Interpreter

Der Interpreter befindet sich im Modul `game-service`.

Das Frontend erstellt aus den verwendeten Code-Blöcken eine Liste von `CodeBlock`-Objekten. Zusammen mit weiteren Informationen wird diese als `ProgramRequest` an den Interpreter gesendet.

Vereinfacht läuft die Verarbeitung folgendermaßen ab:

```text
Code-Editor
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

Die verfügbaren Blocktypen werden modulübergreifend durch `CodeType` im Modul `common` definiert.

Die polymorphe Deserialisierung der Code-Blöcke erfolgt über Jackson.

Die während der Ausführung erzeugten Meldungen werden in einem `ExecutionLog` gesammelt und anschließend als `List<String>` an das Frontend zurückgegeben.

Dadurch können sowohl die Sandbox als auch die Level-Seite erfolgreiche Verarbeitungsschritte und vom Interpreter erkannte Fehler direkt anzeigen.

## Level-Service

Der `level-service` verwaltet die Daten für dynamisch bereitgestellte Level.

Ein Level ist mit einer Programmiersprache verknüpft und besitzt eine Menge verfügbarer Code-Komponenten. Über `LevelComponent` wird zusätzlich gespeichert, wie häufig eine bestimmte Komponente für das jeweilige Level verfügbar sein soll.

Die grundlegende Struktur besteht aus:

```text
Level
    │
    ├── ProgrammingLanguage
    │
    └── LevelComponent
            │
            └── Component
                    │
                    └── CodeType
```

`CodeType` befindet sich im Modul `common`, da die Definition der Blocktypen sowohl vom Interpreter als auch vom Level-Service verwendet wird.

Der Level-Service stellt aktuell REST-Endpunkte bereit, über die Level angelegt und anhand ihrer ID geladen werden können.

Beim Laden eines Levels werden die benötigten Daten zu einem `LevelResponse` zusammengeführt. Dieser enthält neben den Levelinformationen auch die verfügbaren Components und wird vom Frontend zum Aufbau der Level-Seite verwendet.

## Datenbank

Für das Projekt wird **keine SQL-Datei** benötigt.

Die Datenbankstruktur wird beim Start automatisch von Hibernate anhand der vorhandenen Entity-Klassen erstellt:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Neben Benutzern und Klassen werden inzwischen auch Level, Programmiersprachen, Components und die Zuordnung zwischen Leveln und Components über JPA-Entities verwaltet.

---

# Projektstruktur

Das Projekt ist als Maven-Multi-Module-Anwendung aufgebaut. Die wichtigsten Bestandteile sind:

```text
LernspielKB/
│
├── auth-service/       # Authentifizierung sowie Benutzer- und Klassenverwaltung
├── common/             # Modulübergreifend verwendete Typen und Komponenten
├── game-service/       # Interpreter und Ausführung der erstellten Programme
├── level-service/      # Verwaltung und Bereitstellung dynamischer Level
├── lernspiel-app/      # Spring-Boot-Hauptanwendung und Frontend
│
├── .gitignore          # Von Git ignorierte Dateien und Ordner
├── dev.ps1             # Entwicklungs-Skript zum Bauen und Starten
├── README.md           # Projektdokumentation
└── pom.xml             # Parent-POM und Maven-Modulverwaltung
```

<details>

<summary><strong>Vollständige Projektstruktur anzeigen</strong></summary>

```text
LernspielKB/
│
├── .gitignore                                      # Definiert Dateien und Ordner, die von Git ignoriert werden
├── dev.ps1                                         # PowerShell-Skript zum Bauen und Starten der Anwendung
├── pom.xml                                         # Parent-POM zur Verwaltung der Maven-Module
├── README.md                                       # Projektdokumentation
│
├── auth-service/                                   # Authentifizierung, Benutzer- und Klassenverwaltung
│   ├── .gitkeep                                    # Hält den Modulordner bei Bedarf in Git
│   ├── pom.xml                                     # Maven-Konfiguration des Auth-Moduls
│   │
│   └── src/
│       └── main/
│           └── java/
│               └── de/
│                   └── lernspiel/
│                       └── auth/
│                           │
│                           ├── config/                              # Konfiguration von Authentifizierung und Sicherheit
│                           │   ├── AdminBootstrap.java              # Erstellt beim ersten Start automatisch einen Administrator
│                           │   ├── JwtConfig.java                   # Konfiguriert die für JWT benötigten Komponenten
│                           │   └── SecurityConfig.java              # Definiert Zugriffsregeln und Spring-Security-Konfiguration
│                           │
│                           ├── controller/                          # REST-Schnittstellen des Auth-Moduls
│                           │   ├── DebugController.java             # Endpunkte für lokale Entwicklungs- und Debugzwecke
│                           │   ├── SchoolClassController.java       # REST-Endpunkte für die Verwaltung von Klassen
│                           │   └── UserController.java              # REST-Endpunkte für Login und Benutzerverwaltung
│                           │
│                           ├── dto/                                 # Datenobjekte für Requests und Responses
│                           │   ├── LoginRequest.java                # Enthält die für den Login benötigten Zugangsdaten
│                           │   ├── RegisterRequest.java             # Gemeinsame Daten für die Registrierung von Benutzern
│                           │   ├── SchoolClassRequest.java          # Request-Daten zum Erstellen einer Klasse
│                           │   ├── SchoolClassResponse.java         # Response-Daten einer Klasse
│                           │   ├── StudentRegisterRequest.java      # Request-Daten zum Anlegen eines Schülers
│                           │   ├── TeacherRegisterRequest.java      # Request-Daten zum Anlegen eines Lehrers
│                           │   └── UserResponse.java                # Response-Daten eines Benutzers
│                           │
│                           ├── entity/                              # JPA-Entitäten des Auth-Moduls
│                           │   ├── SchoolClass.java                 # Datenbankmodell einer Schulklasse
│                           │   ├── User.java                        # Datenbankmodell eines Benutzers
│                           │   └── UserType.java                    # Definiert die verfügbaren Benutzerrollen
│                           │
│                           ├── repository/                          # Datenbankzugriff über Spring Data JPA
│                           │   ├── SchoolClassRepository.java       # Datenbankzugriff für Schulklassen
│                           │   └── UserRepository.java              # Datenbankzugriff für Benutzer
│                           │
│                           ├── security/                            # JWT-basierte Authentifizierungslogik
│                           │   ├── JwtAuthenticationFilter.java     # Prüft JWTs bei eingehenden Requests
│                           │   └── JwtUtils.java                    # Erstellt, liest und validiert JWTs
│                           │
│                           └── service/                             # Geschäftslogik der Benutzer- und Klassenverwaltung
│                               ├── SchoolClassService.java          # Verarbeitet Operationen rund um Schulklassen
│                               └── UserService.java                 # Verarbeitet Login und Benutzeroperationen
│
├── common/                                         # Modulübergreifend verwendete Definitionen
│   ├── .gitkeep                                    # Hält den Modulordner bei Bedarf in Git
│   ├── pom.xml                                     # Maven-Konfiguration des Common-Moduls
│   │
│   └── src/
│       └── main/
│           └── java/
│               └── de/
│                   └── lernspiel/
│                       └── common/
│                           └── code/
│                               └── CodeType.java    # Zentrale Definition der von mehreren Modulen verwendeten Code-Blocktypen
│
├── game-service/                                   # Spiellogik und eigener Code-Interpreter
│   ├── .gitkeep                                    # Hält den Modulordner bei Bedarf in Git
│   ├── pom.xml                                     # Maven-Konfiguration des Game-Moduls
│   │
│   └── src/
│       └── main/
│           └── java/
│               └── de/
│                   └── lernspiel/
│                       └── game/
│                           │
│                           ├── controller/
│                           │   └── InterpreterController.java       # REST-Endpunkt zum Ausführen eines Programms
│                           │
│                           ├── dto/                                 # Datenmodell des visuellen Programms
│                           │   ├── CodeBlock.java                   # Basisklasse aller Code-Blöcke
│                           │   ├── ElseStatementBlock.java          # Repräsentiert einen Else-Block
│                           │   ├── ExecutionLog.java                # Sammelt Meldungen während der Programmausführung
│                           │   ├── IfStatementBlock.java            # Repräsentiert einen If-Block mit Bedingung und Programm
│                           │   ├── ProgramRequest.java              # Enthält das vom Frontend übermittelte Gesamtprogramm
│                           │   ├── ValueBlock.java                  # Repräsentiert einen konkreten Wert im Programm
│                           │   ├── Variable.java                    # Kapselt Wert und Datentyp einer Variable
│                           │   └── VarNameBlock.java                # Repräsentiert einen Variablennamen
│                           │
│                           └── service/
│                               └── InterpreterService.java          # Interpretiert und verarbeitet die übergebenen Code-Blöcke
│
├── level-service/                                  # Verwaltung und Bereitstellung dynamischer Level
│   ├── pom.xml                                     # Maven-Konfiguration des Level-Moduls
│   │
│   └── src/
│       └── main/
│           └── java/
│               └── de/
│                   └── lernspiel/
│                       └── level/
│                           │
│                           ├── controller/                          # REST-Schnittstellen des Level-Moduls
│                           │   └── LevelController.java             # Endpunkte zum Anlegen und Laden von Leveln
│                           │
│                           ├── dto/                                 # Request- und Response-Objekte für Level
│                           │   ├── CreateLevelRequest.java          # Enthält die Daten zum Anlegen eines neuen Levels
│                           │   ├── LevelComponentRequest.java       # Beschreibt eine beim Erstellen verfügbare Level-Komponente
│                           │   ├── LevelComponentResponse.java      # Beschreibt eine verfügbare Komponente eines geladenen Levels
│                           │   └── LevelResponse.java               # Enthält die vollständigen Leveldaten für das Frontend
│                           │
│                           ├── entity/                              # JPA-Entitäten der Levelverwaltung
│                           │   ├── Component.java                   # Datenbankmodell eines verfügbaren Code-Blocktyps
│                           │   ├── Level.java                       # Datenbankmodell eines Levels mit Aufgabeninformationen
│                           │   ├── LevelComponent.java              # Verknüpft Level und Component inklusive verfügbarer Anzahl
│                           │   └── ProgrammingLanguage.java         # Datenbankmodell einer unterstützten Programmiersprache
│                           │
│                           ├── repository/                          # Datenbankzugriff der Levelverwaltung
│                           │   ├── ComponentRepository.java         # Datenbankzugriff für Code-Komponenten
│                           │   ├── LevelComponentRepository.java    # Datenbankzugriff für die Zuordnung von Leveln und Components
│                           │   ├── LevelRepository.java             # Datenbankzugriff für Level
│                           │   └── ProgrammingLanguageRepository.java # Datenbankzugriff für Programmiersprachen
│                           │
│                           └── service/
│                               └── LevelService.java                # Lädt und erstellt Level inklusive Sprache und Components
│
└── lernspiel-app/                                  # Ausführbare Spring-Boot-Anwendung und Web-Frontend
    ├── .gitkeep                                    # Hält den Modulordner bei Bedarf in Git
    ├── pom.xml                                     # Maven-Konfiguration der Hauptanwendung
    │
    └── src/
        └── main/
            │
            ├── java/
            │   └── de/
            │       └── lernspiel/
            │           └── LernspielApplication.java               # Einstiegspunkt der Spring-Boot-Anwendung
            │
            └── resources/
                │
                ├── application.properties                          # Konfiguration von Spring Boot und Datenbank
                │
                └── static/                                         # Statische Dateien des Web-Frontends
                    │
                    ├── admin.html                                  # Benutzeroberfläche für Administratoren
                    ├── index.html                                  # Login-Seite der Anwendung
                    ├── level.html                                  # Dynamische Level-Seite mit Aufgabenstellung und Code-Editor
                    ├── sandbox.html                                # Frei nutzbarer visueller Code-Editor und Interpreter-Demo
                    ├── student.html                                # Benutzeroberfläche für Schüler
                    ├── teacher.html                                # Benutzeroberfläche für Lehrer
                    │
                    ├── css/
                    │   └── style.css                               # Gemeinsames Styling der Webanwendung
                    │
                    └── js/
                        │
                        ├── api/                                    # Kommunikation zwischen Frontend und Backend
                        │   ├── api.js                               # Allgemeine API-, JWT- und Request-Funktionen
                        │   ├── interpreterApi.js                    # API-Aufruf zur Ausführung eines Programms
                        │   └── levelApi.js                          # API-Aufruf zum Laden eines Levels
                        │
                        ├── auth/                                   # Vorgesehen für gemeinsame Authentifizierungslogik
                        │
                        ├── editor/                                 # Wiederverwendbare Komponenten des Code-Editors
                        │   ├── blockDefinitions.js                  # Zentrale Definition und Darstellung verfügbarer Code-Blöcke
                        │   ├── blockFactory.js                      # Erzeugt Blockobjekte für das Programm
                        │   ├── consoleTheme.js                      # Verwaltet die Darstellungsmodi der Interpreter-Konsole
                        │   ├── dragDrop.js                          # Steuert Drag & Drop, Verschieben und Löschen von Blöcken
                        │   ├── editorState.js                       # Verwaltet den aktuellen Zustand des Programms
                        │   └── renderer.js                          # Rendert den Editor-State als sichtbare Codezeilen
                        │
                        └── pages/                                  # Seitenspezifische JavaScript-Einstiegspunkte
                            ├── admin.js                             # Logik der Administrator-Seite
                            ├── level.js                             # Lädt Leveldaten und verbindet sie mit den Editor-Modulen
                            ├── login.js                             # Login und Weiterleitung nach Benutzerrolle
                            ├── sandbox.js                           # Verbindet Editor, Drag & Drop und Interpreter der Sandbox
                            ├── student.js                           # Logik der Schüler-Seite
                            └── teacher.js                           # Logik der Lehrer-Seite
```

> **Hinweis:** Generierte Build-Artefakte (`target/`) sowie lokale Entwicklungsdateien und Tool-Caches wie `.aider.*`, `.aider.tags.cache.v4/` und `.VSCodeCounter/` sind in dieser Übersicht bewusst nicht aufgeführt.

</details>

---

# API-Endpunkte

Die wichtigsten Schnittstellen der Anwendung sind nachfolgend dokumentiert.

<details>

<summary><strong>Authentifizierung anzeigen</strong></summary>

### Authentifizierung

| Methode | Endpunkt | Beschreibung | Berechtigung |
| ------- | -------- | ------------ | ------------ |
| `POST` | `/api/benutzer/login` | Benutzer anmelden und JWT erhalten | Öffentlich |
| `GET` | `/api/benutzer/me` | Aktuell angemeldeten Benutzer abrufen | Authentifiziert |

</details>

<details>

<summary><strong>Benutzerverwaltung anzeigen</strong></summary>

### Benutzerverwaltung

| Methode | Endpunkt | Beschreibung | Berechtigung |
| ------- | -------- | ------------ | ------------ |
| `POST` | `/api/benutzer/register/teacher` | Lehrer anlegen | Admin |
| `POST` | `/api/benutzer/register/student` | Schüler anlegen | Admin, Teacher |
| `GET` | `/api/benutzer` | Alle Benutzer anzeigen | Admin |
| `DELETE` | `/api/benutzer/{userID}` | Benutzer löschen | Admin |
| `GET` | `/api/benutzer/me/students` | Eigene Schüler eines Lehrers abrufen | Teacher |

</details>

<details>

<summary><strong>Klassenverwaltung anzeigen</strong></summary>

### Klassenverwaltung

| Methode | Endpunkt | Beschreibung | Berechtigung |
| ------- | -------- | ------------ | ------------ |
| `POST` | `/api/classes` | Neue Klasse anlegen | Admin, Teacher |
| `GET` | `/api/classes` | Alle Klassen abrufen | Authentifiziert |
| `GET` | `/api/classes/{id}` | Einzelne Klasse abrufen | Authentifiziert |

</details>

<details>

<summary><strong>Level-Endpunkte anzeigen</strong></summary>

### Level

| Methode | Endpunkt | Beschreibung |
| ------- | -------- | ------------ |
| `GET` | `/api/levels/{levelID}` | Lädt ein Level inklusive Programmiersprache und verfügbarer Code-Komponenten |
| `POST` | `/api/levels` | Legt ein neues Level inklusive Programmiersprache und verfügbarer Code-Komponenten an |

Der `GET`-Endpunkt liefert die für die dynamische Level-Seite benötigten Daten.

Beispiel:

```json
{
  "levelID": 1,
  "levelName": "Erste Variable",
  "levelDescription": "Erstelle eine int-Variable x mit dem Wert 5.",
  "category": "BASICS",
  "levelNumber": 1,
  "languageID": 1,
  "language": "JAVA",
  "components": [
    {
      "type": "INT",
      "amount": 1
    },
    {
      "type": "VAR_NAME",
      "amount": 1
    },
    {
      "type": "EQUALS",
      "amount": 1
    },
    {
      "type": "VALUE",
      "amount": 1
    },
    {
      "type": "BREAK",
      "amount": 1
    }
  ]
}
```

Über `POST /api/levels` können Level aktuell beispielsweise für Entwicklungs- und Testzwecke angelegt werden.

Beispiel:

```json
{
  "levelName": "Erste Variable",
  "levelDescription": "Erstelle eine int-Variable x mit dem Wert 5.",
  "category": "BASICS",
  "levelNumber": 1,
  "language": "JAVA",
  "components": [
    {
      "type": "INT",
      "amount": 1
    },
    {
      "type": "VAR_NAME",
      "amount": 1
    },
    {
      "type": "EQUALS",
      "amount": 1
    },
    {
      "type": "VALUE",
      "amount": 1
    },
    {
      "type": "BREAK",
      "amount": 1
    }
  ]
}
```

</details>

<details>

<summary><strong>Interpreter-Endpunkt anzeigen</strong></summary>

### Interpreter

| Methode | Endpunkt | Beschreibung |
| ------- | -------- | ------------ |
| `POST` | `/game/interpreter/run` | Führt ein aus Code-Blöcken bestehendes Programm über den Interpreter aus |

Der Endpunkt erwartet einen `ProgramRequest` im Request-Body.

Beispiel:

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

</details>

<details>

<summary><strong>Debug-Endpunkte anzeigen</strong></summary>

### Debug-Endpunkte

Die folgenden Endpunkte dienen ausschließlich der lokalen Entwicklung und zum Testen der Benutzerverwaltung.

| Methode | Endpunkt | Beschreibung |
| ------- | -------- | ------------ |
| `GET` | `/debug/db-info` | Zeigt Informationen über die aktuell verbundene Datenbank (Datenbankname, Benutzer, Host, Port und Version). |
| `GET` | `/debug/create-test-user` | Erstellt einen Testbenutzer in der Datenbank. |
| `GET` | `/debug/list-users` | Gibt eine Liste aller gespeicherten Benutzer zurück. |
| `DELETE` | `/debug/drop-user` | Löscht die Tabelle `user`. **Nur für Entwicklungszwecke verwenden!** |
| `GET` | `/debug/login?userID={id}&password={passwort}` | Testet den Login eines Benutzers und gibt bei Erfolg ein JWT zurück. |

</details>

---

# Entwicklung

Für die alltägliche Entwicklung kann das im Projekt enthaltene PowerShell-Skript `dev.ps1` verwendet werden.

Die Einrichtung und Verwendung des Skripts ist unter [Projekt starten](#projekt-starten) beschrieben.

## Manueller Start

Alternativ kann das Projekt vollständig über Maven gebaut und gestartet werden.

Zunächst das gesamte Multi-Module-Projekt bauen:

```bash
mvn clean install
```

Anschließend das Spring-Boot-Modul starten:

```bash
cd lernspiel-app
mvn spring-boot:run
```

Die Anwendung ist anschließend erreichbar unter:

```text
http://localhost:8080
```

## Aktueller Entwicklungsstand

Die grundlegende Architektur des Projekts ist vorhanden.

Aktuell umgesetzt sind unter anderem:

- Authentifizierung über JWT
- Benutzerverwaltung
- Rollen für Administratoren, Lehrer und Schüler
- Klassenverwaltung
- Multi-Module-Struktur mit Maven
- `common` für modulübergreifend verwendete Definitionen
- zentraler `CodeType` für gemeinsam verwendete Code-Blocktypen
- `game-service` für die Spiellogik
- Interpreter für die Verarbeitung von Code-Blöcken
- polymorphe `CodeBlock`-DTOs
- Code-Sandbox mit Drag & Drop
- modularisierte und wiederverwendbare Editor-Komponenten
- mehrzeilige Programme
- Verschieben und Löschen von Code-Blöcken
- Übertragung der Programme an das Backend
- Ausgabe des Interpreters im Frontend
- Darstellung von Interpreterfehlern
- mehrere Darstellungsmodi für die Interpreter-Konsole
- `level-service` für dynamisch bereitgestellte Level
- JPA-Entities für Level, Programmiersprachen und Components
- Zuordnung verfügbarer Components und Mengen zu einzelnen Leveln
- REST-Endpunkte zum Erstellen und Laden von Leveln
- dynamische Level-Seite
- Laden von Levelinformationen aus der Datenbank
- dynamischer Aufbau der Block-Palette anhand des geladenen Levels
- Wiederverwendung der Sandbox-Editor-Module innerhalb der Level-Seite
- Übergabe der Level- und Sprachinformationen an den Interpreter

Die Code-Sandbox bleibt als frei nutzbare Entwicklungs- und Demonstrationsumgebung für den visuellen Editor bestehen.

Parallel dazu bildet die dynamische Level-Seite inzwischen die Grundlage für das eigentliche Lernspiel. Level können aus der Datenbank geladen werden und geben Aufgabenstellung, Kategorie, Programmiersprache und verfügbare Code-Blöcke vor.

Noch nicht vollständig umgesetzt sind unter anderem die tatsächliche Begrenzung der verfügbaren Blockanzahl anhand des gespeicherten `amount`-Werts, die benutzerabhängige Bereitstellung und Freischaltung von Leveln sowie die abschließende Prüfung einer Level-Lösung.