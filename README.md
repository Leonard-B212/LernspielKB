# Lernspiel

Webbasiertes Lernspiel für Schülerinnen und Schüler der Klassenstufen 9–10 zum spielerischen Erlernen grundlegender Programmierkonzepte.

Das Projekt entsteht im Rahmen eines Projekts an der DHBW und kombiniert einen visuellen Drag-&-Drop-Code-Editor mit einem eigenen Interpreter, dynamisch bereitgestellten Leveln und einem visuellen Skilltree.

---

## Inhalt

- [Überblick](#überblick)
- [Schnellstart](#schnellstart)
  - [Voraussetzungen](#voraussetzungen)
  - [Repository klonen](#repository-klonen)
  - [Anwendung konfigurieren](#anwendung-konfigurieren)
  - [Projekt starten](#projekt-starten)
  - [Test-Zugangsdaten](#test-zugangsdaten)
- [Bedienung und Testen](#bedienung-und-testen)
  - [Benutzerrollen](#benutzerrollen)
  - [Schüler-Lernpfad](#schüler-lernpfad)
  - [Skilltree](#skilltree)
  - [Code-Sandbox](#code-sandbox)
  - [Dynamische Level](#dynamische-level)
- [Architektur](#architektur)
  - [Frontend](#frontend)
  - [Skilltree-Architektur](#skilltree-architektur)
  - [Interpreter](#interpreter)
  - [Level-Service](#level-service)
  - [Level-Bootstrap](#level-bootstrap)
  - [Level-Fortschritt](#level-fortschritt)
  - [Datenbank](#datenbank)
- [Projektstruktur](#projektstruktur)
- [API-Endpunkte](#api-endpunkte)
- [Entwicklung](#entwicklung)
  - [Manueller Start](#manueller-start)
  - [XSS Security Check](#xss-security-check)
  - [Aktueller Entwicklungsstand](#aktueller-entwicklungsstand)
  - [Noch offene Punkte](#noch-offene-punkte)

---

# Überblick

Das Lernspiel soll Schülerinnen und Schülern grundlegende Programmierkonzepte vermitteln.

Programme werden dabei nicht direkt als Quelltext eingegeben. Stattdessen können Code-Blöcke per Drag & Drop zu einem Programm zusammengesetzt werden. Das erstellte Programm wird anschließend an einen eigenen Interpreter übergeben und ausgewertet.

Die eigentliche Schüleroberfläche wird über einen visuellen Skilltree dargestellt. Dort können Level nach Programmiersprache und Kategorie ausgewählt werden. Der Skilltree bildet damit den zentralen Einstiegspunkt für Schülerinnen und Schüler.

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
- REST-Schnittstellen zum Anlegen und Laden von Leveln
- dynamisches Laden von Leveln im Frontend
- dynamische Bereitstellung der für ein Level verfügbaren Code-Blöcke
- automatische Ergänzung zufälliger zusätzlicher Code-Blöcke
- zufällige Anordnung der Block-Palette
- Programmiersprachen als eigene Datenbankobjekte
- Level-Kategorien mit definierter Reihenfolge
- automatischer Bootstrap vordefinierter Level
- automatische Erkennung neuer Levelgruppen über Spring
- Level-Übersicht für den Skilltree
- visueller Skilltree als zentrale Schüleroberfläche
- Wechsel zwischen verschiedenen Programmiersprachen im Skilltree
- dynamische Gruppierung von Leveln nach Kategorien
- physikbasierte leichte Bewegung der Skilltree-Nodes
- strukturierte ExecutionLogs mit typisierten Log-Einträgen
- serverseitige automatische Level-Verifikation
- automatische Speicherung erfolgreich abgeschlossener Level
- visuelle Verbindungen zwischen Kategorien und Leveln
- Speicherung abgeschlossener Level
- benutzerbezogener Level-Fortschritt
- gemeinsame Navigation zwischen Lernpfad, Level und Sandbox
- Logout über die gemeinsame Navigation
- automatisierter XSS-Codecheck als Entwicklungshilfe

Die Code-Sandbox dient weiterhin als frei nutzbare technische Umgebung für den visuellen Editor und Interpreter.

Zusätzlich können Level dynamisch aus der Datenbank geladen werden. Ein Level definiert unter anderem Aufgabenstellung, Kategorie, Programmiersprache und die für die Lösung vorgesehenen Code-Blöcke.

Vordefinierte Standardlevel werden beim Start der Anwendung automatisch geprüft und bei Bedarf angelegt.

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

## Anwendung konfigurieren

Vor dem ersten Start die Datei `lernspiel-app/src/main/resources/application.properties.example` kopieren und in `application.properties` umbenennen.

Anschließend die enthaltenen Platzhalter für Datenbankzugang, Passwörter und Secrets an die eigene Umgebung anpassen.

Die `application.properties` wird über `.gitignore` nicht versioniert.

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
\==========================================
          Lernspiel Entwicklung
\==========================================
[1] Clean Install
[2] Clean Install und Start
[3] Nur Start
[4] XSS Security Check
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

Der XSS Security Check kann unabhängig vom Start der Anwendung über Menüpunkt `[4]` ausgeführt werden.

## Test-Zugangsdaten

Beim ersten Start wird automatisch ein Administrator angelegt, sofern noch kein Administrator in der Datenbank vorhanden ist.

| Benutzer-ID | Passwort |
| ----------- | -------- |
| `1` | `admin123` |

> **Hinweis:** Bei einer neu angelegten bzw. leeren Datenbank wird der Administrator beim Start automatisch erzeugt. In einer regulär neu erzeugten Datenbank erhält dieser dadurch die Benutzer-ID `1`.

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

Für Schüler dient der Skilltree als zentrale Startseite des Lernspiels.

Nach erfolgreichem Login wird ein Schüler direkt zum Lernpfad weitergeleitet.

Von dort kann er:

- zwischen unterstützten Programmiersprachen wechseln
- Kategorien des Lernpfads betrachten
- verfügbare Level auswählen
- bereits abgeschlossene Level erkennen
- ein Level öffnen
- zur freien Code-Sandbox wechseln
- sich aus der Anwendung abmelden

Der grundlegende Ablauf ist:

```text
Login
  ↓
Skilltree / Lernpfad
  ↓
Level auswählen
  ↓
Level bearbeiten
  ↓
zurück zum Lernpfad
```

## Schüler-Lernpfad

Nach dem Login wird ein Benutzer mit der Rolle `STUDENT` direkt auf folgende Seite weitergeleitet:

```text
http://localhost:8080/skilltree.html
```

Der bisherige zusätzliche Umweg über eine separate Schülerübersicht ist für den eigentlichen Lernablauf nicht notwendig.

Der Skilltree bildet deshalb den zentralen Ausgangspunkt für Schüler.

Von einem geöffneten Level kann jederzeit über die Navigation zum Lernpfad zurückgekehrt werden.

Zusätzlich ist die Sandbox über die gemeinsame Navigation erreichbar.

## Skilltree

Der Skilltree stellt die verfügbaren Level als visuellen Lernpfad dar.

Dabei werden die Level nicht lediglich als Liste dargestellt, sondern anhand ihrer Programmiersprache, Kategorie und Reihenfolge strukturiert.

Vereinfacht ergibt sich folgende Struktur:

```text
Programmiersprache
      │
      ├── Kategorie 1
      │      ├── Level 1
      │      ├── Level 2
      │      └── Level 3
      │
      ├── Kategorie 2
      │      ├── Level 1
      │      └── Level 2
      │
      └── ...
```

### Programmiersprachen

Level sind jeweils einer Programmiersprache zugeordnet.

Der Skilltree kann dadurch abhängig von der ausgewählten Sprache unterschiedliche Level darstellen.

Beispielsweise können getrennte Lernpfade für:

- Java
- Python
- weitere zukünftige Sprachen

bereitgestellt werden.

Die verfügbaren Sprachen werden anhand der vorhandenen Level bestimmt und im Frontend auswählbar dargestellt.

### Kategorien

Level sind zusätzlich einer `LevelCategory` zugeordnet.

Eine Kategorie besitzt eine definierte Reihenfolge und bildet einen größeren Abschnitt innerhalb des Lernpfads.

Beispiele können sein:

```text
BASICS
VARIABLES
CONDITIONS
LOOPS
...
```

Die Kategorien bilden damit die grobe Reihenfolge des Lernpfads.

Innerhalb einer Kategorie bestimmt die `levelNumber` die Reihenfolge der einzelnen Level.

Die Kategorie selbst dient grundsätzlich als wiederverwendbare Struktur. Welche Kategorien tatsächlich innerhalb eines Sprach-Lernpfads erscheinen, ergibt sich aus den vorhandenen Leveln der jeweiligen Programmiersprache.

Dadurch müssen Kategorien nicht für jede Programmiersprache vollständig dupliziert werden.

Sollte zukünftig eine vollständig unterschiedliche Kategoriereihenfolge pro Programmiersprache benötigt werden, müsste diese Zuordnung entsprechend erweitert werden.

### Visuelle Darstellung

Die Kategorien werden als größere zentrale Nodes dargestellt.

Die einzelnen Level sind als kleinere Nodes um die jeweilige Kategorie angeordnet.

SVG-Verbindungen stellen die Beziehungen zwischen:

- Kategorie und Kategorie
- Kategorie und Level

dar.

Zusätzlich besitzt der Skilltree eine leichte Physics-Simulation.

Die Level-Nodes können sich dadurch beim Laden geringfügig voneinander abstoßen und natürlicher verteilen.

Die Kategorien selbst bleiben an ihrer vorgesehenen Position und wirken gleichzeitig als Hindernisse für die Level-Nodes.

Beim Hover über einen Level-Node wird dieser leicht hervorgehoben. Die Verbindungslinien werden während der kurzen Physics-Simulation beim Laden aktualisiert.

Die Physics dient ausschließlich der visuellen Darstellung. Die fachliche Reihenfolge der Level wird weiterhin durch die Backend-Daten bestimmt.

### Level-Fortschritt

Abgeschlossene Level können benutzerbezogen gespeichert werden.

Der Skilltree kann dadurch erkennen, welche Level der aktuell angemeldete Schüler bereits abgeschlossen hat und diese entsprechend darstellen.

Eine endgültige fachliche Regelung zur Freischaltung bzw. Sperrung zukünftiger Level ist aktuell noch nicht festgelegt.

Insbesondere existiert derzeit bewusst keine feste `requires`-Abhängigkeit zwischen einzelnen Leveln.

Die Architektur wurde so gehalten, dass eine spätere Progress- oder Unlock-Logik ergänzt werden kann, ohne die grundlegende Skilltree-Darstellung neu aufbauen zu müssen.

---

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

Die Sandbox ist direkt erreichbar unter:

```text
http://localhost:8080/sandbox.html
```

Zusätzlich kann sie über die gemeinsame Navigation aus dem Lernpfad bzw. aus einem Level geöffnet werden.

---

## Dynamische Level

Neben der frei nutzbaren Sandbox können Level dynamisch aus der Datenbank geladen werden.

Ein Level enthält aktuell unter anderem:

- einen Namen
- eine Beschreibung bzw. Aufgabenstellung
- eine Kategorie
- eine Levelnummer
- eine Programmiersprache
- die für die Lösung vorgesehenen Code-Blöcke
- die konfigurierte Anzahl der jeweiligen Code-Blöcke
- einen erwarteten ExecutionLog zur serverseitigen Prüfung der Lösung

Die Kombination aus Kategorie, Levelnummer und Programmiersprache dient zur strukturierten Einordnung eines Levels.

Die Level-Seite verwendet dieselben wiederverwendbaren Editor-Komponenten wie die Sandbox.

Die Block-Palette wird jedoch nicht statisch im HTML definiert, sondern dynamisch aus den Daten des geladenen Levels aufgebaut.

Damit die für ein Level gespeicherten Komponenten nicht unmittelbar die Lösung vorgeben, wird die Palette vor der Darstellung zusätzlich erweitert und gemischt.

Der Ablauf ist:

```text
Level-Components aus dem Backend
          ↓
2–3 zufällige zusätzliche Blocktypen
          ↓
zufällige Reihenfolge der gesamten Palette
          ↓
Darstellung im Level
```

Die zusätzlichen Blöcke werden ausschließlich für die sichtbare Palette erzeugt. Die eigentliche Leveldefinition und die in der Datenbank gespeicherten `LevelComponent`-Einträge werden dadurch nicht verändert.

Bereits im Level vorhandene Blocktypen werden bei der Auswahl der zusätzlichen Blöcke ausgeschlossen.

Als mögliche zusätzliche Blocktypen werden nur Blocktypen verwendet, die das Frontend über die zentralen `BLOCK\_DEFINITIONS` aktuell unterstützt.

Die gesamte Palette wird anschließend mit einem Fisher-Yates-Shuffle zufällig angeordnet.

Dadurch sollen weder die Auswahl der verfügbaren Blöcke noch deren Reihenfolge die Lösung einer Aufgabe direkt vorgeben.

Die entsprechende Logik befindet sich in:

```text
js/editor/paletteBuilder.js
```

Ein Level kann über seine ID geladen werden:

```text
http://localhost:8080/level.html?id=1
```

Die Level-Seite lädt anschließend die zugehörigen Daten über den Level-Service und zeigt unter anderem:

- Aufgabenstellung
- Kategorie
- Programmiersprache
- verfügbare Code-Blöcke

an.

Die in der Datenbank gespeicherte Anzahl der vorgesehenen Komponenten wird bereits an das Frontend übertragen.

Eine tatsächliche Begrenzung der maximal verwendbaren Blockanzahl im Editor ist aktuell noch nicht vollständig umgesetzt.

---

# Architektur

Das Projekt ist als Maven-Multi-Module-Anwendung aufgebaut.

Die einzelnen Module trennen Authentifizierung, gemeinsam verwendete Komponenten, Spiellogik, Levelverwaltung und die ausführbare Spring-Boot-Anwendung logisch voneinander.

Die Hauptmodule sind:

```text
auth-service
     │
     ├── Authentifizierung
     ├── Benutzer
     └── Klassen
common
     │
     └── gemeinsam verwendete Typen
game-service
     │
     └── Interpreter
level-service
     │
     ├── Level
     ├── Kategorien
     ├── Programmiersprachen
     ├── Components
     ├── Level-Bootstrap
     └── Fortschritt
lernspiel-app
     │
     ├── Spring-Boot-Anwendung
     └── Frontend
```

## Frontend

Das Frontend wurde mit **HTML, CSS und Vanilla JavaScript** umgesetzt.

Neben der Benutzerverwaltung enthält es:

- Login
- Administratoroberfläche
- Lehreroberfläche
- visuellen Code-Editor
- Sandbox
- dynamische Level-Seite
- Skilltree
- gemeinsame Navigation

Die JavaScript-Komponenten sind nach ihrer jeweiligen Aufgabe gegliedert.

```text
js/
│
├── api/          # Kommunikation mit dem Backend
├── editor/       # Wiederverwendbarer visueller Code-Editor und Palette
├── navigation/   # Gemeinsame Navigation
├── pages/        # Seitenspezifische Controller
└── skilltree/    # Darstellung und Zustand des Skilltrees
```

### API-Schicht

Die Dateien unter `js/api/` kapseln die Kommunikation mit dem Backend.

Dazu gehören unter anderem:

- allgemeine authentifizierte Requests
- JWT-Verwaltung
- Interpreter-Aufrufe
- Level-Aufrufe
- Progress-Aufrufe

### Editor

Die Editor-Komponenten unter `js/editor/` werden sowohl von der Sandbox als auch von der dynamischen Level-Seite verwendet.

Dadurch müssen Drag & Drop, Rendering und Editor-State nicht für jede Seite separat implementiert werden.

Zusätzlich befindet sich dort mit `paletteBuilder.js` die Logik für den dynamischen Aufbau der Block-Palette eines Levels.

Der `paletteBuilder`:

- übernimmt die vom Backend gelieferten Level-Komponenten
- bestimmt weitere verfügbare Blocktypen über `BLOCK\_DEFINITIONS`
- schließt bereits vorhandene Typen als zusätzliche Blöcke aus
- ergänzt zufällig zwei bis drei zusätzliche Blocktypen
- mischt die vollständige Palette
- verändert die ursprünglichen Leveldaten nicht

### Navigation

Die gemeinsame Navigation befindet sich unter:

```text
js/navigation/navigation.js
```

Sie verbindet die zentralen Schülerbereiche miteinander und bindet unter anderem die gemeinsame Logout-Funktion ein.

Der grundlegende Navigationsfluss ist:

```text
Skilltree
   │
   ├──────────────→ Sandbox
   │
   └──→ Level
          │
          ├────────→ Sandbox
          │
          └────────→ Skilltree
```

---

## Skilltree-Architektur

Die Skilltree-Logik ist bewusst in mehrere JavaScript-Dateien getrennt.

```text
skilltree.js
     │
     ├── skilltreeState.js
     ├── skilltreeRenderer.js
     └── skilltreePhysics.js
```

### `skilltree.js`

`skilltree.js` ist der seitenspezifische Einstiegspunkt.

Die Datei:

- prüft den angemeldeten Benutzer
- lädt die benötigten Daten
- verbindet API, State und Renderer
- verarbeitet den Sprachwechsel
- verarbeitet die Auswahl eines Levels

### `skilltreeState.js`

Der State verwaltet den aktuellen Zustand des Skilltrees.

Dazu gehören beispielsweise:

- geladene Level
- aktive Programmiersprache
- abgeschlossene Level

Die Darstellung bleibt dadurch von der Datenhaltung getrennt.

### `skilltreeRenderer.js`

Der Renderer ist für die sichtbare Darstellung verantwortlich.

Er:

- gruppiert Level nach Kategorie
- sortiert Kategorien
- sortiert Level innerhalb einer Kategorie
- erzeugt Kategorie-Nodes
- erzeugt Level-Nodes
- positioniert die Nodes
- erzeugt SVG-Verbindungen
- aktualisiert Verbindungen bei Bewegung
- markiert abgeschlossene Level

### `skilltreePhysics.js`

Die Physics-Schicht ergänzt die statische Grundposition der Level-Nodes um leichte physikalische Bewegung.

Sie dient ausschließlich der visuellen Darstellung.

Unter anderem werden:

- Abstände zwischen Level-Nodes berücksichtigt
- Überschneidungen reduziert
- Kategorie-Nodes als statische Hindernisse behandelt
- leichte Bewegungen der Level-Nodes ermöglicht

Die fachliche Reihenfolge der Level wird dadurch nicht verändert.

---

## Interpreter

Der Interpreter befindet sich im Modul `game-service`.

Das Frontend erstellt aus den verwendeten Code-Blöcken eine Liste von `CodeBlock`-Objekten.

Zusammen mit weiteren Informationen wird diese als `ProgramRequest` an den Interpreter gesendet.

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
strukturierte LogFile-Einträge
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

Die während der Ausführung erzeugten Meldungen werden als strukturierte `LogFile`-Einträge in einem `ExecutionLog` gesammelt und an das Frontend zurückgegeben. `ExecutionLog`, `LogFile` und `LogType` befinden sich im Modul `common`, da die Ausführungsdaten sowohl vom Interpreter als auch von der Level-Verifikation verwendet werden.

Dadurch können sowohl die Sandbox als auch die Level-Seite erfolgreiche Verarbeitungsschritte und vom Interpreter erkannte Fehler direkt anzeigen.

---

## Level-Verifikation

Nach der Ausführung eines Levels wird der vom Interpreter erzeugte `ExecutionLog` serverseitig mit dem für das Level hinterlegten erwarteten `ExecutionLog` verglichen.

```text
Level-Seite
    ↓
Interpreter
    ↓
tatsächlicher ExecutionLog
    ↓
LevelVerificationController
    ↓
LevelVerificationService
    ↓
erwarteter ExecutionLog aus dem Level
    ↓
Level erfolgreich / nicht erfolgreich
```

Das Frontend sendet für die Prüfung ausschließlich die Level-ID und den tatsächlich erzeugten `ExecutionLog`. Der erwartete ExecutionLog wird nicht an das Schüler-Frontend ausgeliefert, sondern im Backend anhand der Level-ID geladen.

Bei erfolgreicher Verifikation wird das Level anschließend über die Progress-Schnittstelle für den aktuell authentifizierten Benutzer als abgeschlossen gespeichert.

## Level-Service

Der `level-service` verwaltet die Daten für dynamisch bereitgestellte Level und den zugehörigen Lernfortschritt.

Ein Level ist mit:

- einer Programmiersprache
- einer Kategorie
- verfügbaren Code-Komponenten
- einem erwarteten `ExecutionLog`

verknüpft.

Über `LevelComponent` wird zusätzlich gespeichert, wie häufig eine bestimmte Komponente für das jeweilige Level verfügbar sein soll.

Die grundlegende Struktur besteht aus:

```text
Level
  │
  ├── ProgrammingLanguage
  │
  ├── LevelCategory
  │
  └── LevelComponent
          │
          └── Component
                  │
                  └── CodeType
```

`CodeType` befindet sich im Modul `common`, da die Definition der Blocktypen sowohl vom Interpreter als auch vom Level-Service verwendet wird.

Der erwartete `ExecutionLog` eines Levels wird über den `ExecutionLogConverter` als JSON in der Datenbank gespeichert. Er ist Bestandteil der Leveldefinition, wird jedoch nicht über die normalen Level-Responses an das Schüler-Frontend ausgeliefert.

### LevelCategory

`LevelCategory` bildet die übergeordnete Struktur des Lernpfads.

Eine Kategorie besitzt eine definierte Reihenfolge.

Die einzelnen Level besitzen zusätzlich eine `levelNumber`.

Dadurch entsteht eine zweistufige Sortierung:

```text
categoryOrder
      ↓
Kategorie
      ↓
levelNumber
      ↓
Level
```

Der Level-Service stellt neben vollständigen Leveldaten auch kompakte Übersichtsobjekte über `LevelOverviewResponse` bereit.

Diese enthalten nur die Informationen, die für Übersichtsseiten wie den Skilltree benötigt werden.

Dazu gehören unter anderem:

- Level-ID
- Levelname
- Kategorie-ID
- Kategoriename
- Kategoriereihenfolge
- Levelnummer
- Sprach-ID
- Programmiersprache

Dadurch muss der Skilltree nicht für jedes Level sämtliche Editor- und Component-Daten laden.

---

## Level-Bootstrap

Vordefinierte Standardlevel werden beim Start der Anwendung automatisch über den Level-Bootstrap geprüft.

Die grundlegende Struktur ist:

```text
LevelBootstrap
      ↓
List<LevelDefinitionProvider>
      ↓
JavaBasicLevels
JavaVariableLevels
JavaExpressionLevels
JavaAssignmentLevels
weitere zukünftige Provider
      ↓
LevelService
      ↓
Datenbank
```

`LevelBootstrap` implementiert `CommandLineRunner` und wird dadurch beim Start der Spring-Boot-Anwendung ausgeführt.

Die konkreten Leveldefinitionen sind vom eigentlichen Bootstrap getrennt.

Das Interface:

```text
LevelDefinitionProvider
```

definiert dafür eine gemeinsame Schnittstelle.

Die einzelnen Levelgruppen, aktuell beispielsweise:

```text
JavaBasicLevels
JavaVariableLevels
JavaExpressionLevels
JavaAssignmentLevels
```

implementieren dieses Interface und werden als Spring-Komponenten registriert.

Spring stellt dem `LevelBootstrap` automatisch alle vorhandenen `LevelDefinitionProvider` zur Verfügung.

Dadurch muss `LevelBootstrap` keine konkreten Levelgruppen kennen.

Wird zukünftig beispielsweise eine weitere Gruppe angelegt:

```text
JavaConditionLevels
```

muss diese lediglich:

- `LevelDefinitionProvider` implementieren
- als Spring-Komponente registriert werden
- ihre Level über `createLevels()` bereitstellen

Eine zusätzliche manuelle Registrierung in `LevelBootstrap` ist nicht notwendig.

Für jedes definierte Level wird beim Start geprüft, ob bereits ein Level mit der entsprechenden Kombination aus:

```text
Programmiersprache
+
Kategorie
+
Levelnummer
```

existiert.

Ist das Level bereits vorhanden, wird es nicht erneut angelegt.

Fehlt es, wird es über den `LevelService` erstellt.

Dadurch kann der Bootstrap bei jedem Start ausgeführt werden, ohne bestehende Standardlevel zu duplizieren.

Nach Abschluss des Bootstraps wird im Terminal ausgegeben, ob alle Level bereits vorhanden waren oder neue Level angelegt wurden.

Die Bootstrap-Definitionen dienen damit als zentrale Definition der mitgelieferten Standardlevel.

Zusätzlich stellt `ExpectedExecutionLogs` Hilfsmethoden für die erwarteten strukturierten Ausführungslogs der Standardlevel bereit. Die Provider definieren damit neben Aufgabenstellung und verfügbaren Komponenten auch die für eine erfolgreiche Lösung erwarteten Interpreter-Ereignisse.

Der Bootstrap legt aktuell ausschließlich fehlende Level an. Bereits vorhandene Level werden beim Start nicht automatisch mit geänderten Bootstrap-Definitionen synchronisiert.

Der REST-Endpunkt zum manuellen Anlegen von Leveln bleibt unabhängig davon weiterhin vorhanden.

---

## Level-Fortschritt

Der Fortschritt eines Schülers wird getrennt von den eigentlichen Leveldaten verwaltet.

Dafür existiert die Entity:

```text
CompletedLevel
```

Sie bildet die Grundlage für die Speicherung bereits abgeschlossener Level.

Die Progress-Architektur besteht aus:

```text
LevelProgressController
        ↓
LevelProgressService
        ↓
CompletedLevelRepository
        ↓
CompletedLevel
```

Für die Kommunikation mit dem Frontend existiert zusätzlich:

```text
LevelProgressResponse
```

Im Frontend werden die Progress-Daten über:

```text
progressApi.js
```

geladen.

Nach erfolgreicher Level-Verifikation wird das Level über die Progress-Schnittstelle für den aktuell authentifizierten Benutzer als abgeschlossen gespeichert.

Der Skilltree kann damit feststellen, welche Level für den aktuell angemeldeten Benutzer bereits abgeschlossen wurden.

Wichtig ist die Trennung zwischen:

```text
Fortschritt speichern
```

und:

```text
Level freischalten / sperren
```

Die technische Grundlage zur Speicherung abgeschlossener Level ist vorhanden.

Eine endgültige fachliche Unlock-Logik ist dagegen noch nicht festgelegt.

Es gibt aktuell bewusst keine feste `requires`-Beziehung zwischen einzelnen Leveln.

Dadurch kann später entschieden werden, ob beispielsweise:

- alle Level frei verfügbar sind
- Kategorien nacheinander freigeschaltet werden
- Level anhand vorheriger Level freigeschaltet werden
- Fortschritt nur visuell dargestellt wird

ohne die grundlegende Level- und Skilltree-Architektur ersetzen zu müssen.

---

## Datenbank

Für das Projekt wird **keine SQL-Datei** benötigt.

Die Datenbankstruktur wird beim Start automatisch von Hibernate anhand der vorhandenen Entity-Klassen erstellt:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Über JPA-Entities werden unter anderem verwaltet:

```text
User
SchoolClass
Level
LevelCategory
ProgrammingLanguage
Component
LevelComponent
CompletedLevel
```

Die Datenbankstruktur ergibt sich damit direkt aus dem aktuellen Stand der Entity-Klassen.

Zusätzlich werden die mitgelieferten Standardlevel über den `LevelBootstrap` geprüft und bei Bedarf angelegt.

---

# Projektstruktur

Das Projekt ist als Maven-Multi-Module-Anwendung aufgebaut.

Die wichtigsten Bestandteile sind:

```text
LernspielKB/
│
├── auth-service/       # Authentifizierung sowie Benutzer- und Klassenverwaltung
├── common/             # Modulübergreifend verwendete Code- und ExecutionLog-Typen
├── game-service/       # Interpreter und Ausführung der erstellten Programme
├── level-service/      # Level, Bootstrap, Verifikation, Kategorien, Programmiersprachen und Fortschritt
├── lernspiel-app/      # Spring-Boot-Hauptanwendung und Frontend
│
├── .gitignore          # Von Git ignorierte Dateien und Ordner
├── dev.ps1             # Entwicklungs-Skript zum Bauen, Starten und für Security-Checks
├── xss-check.ps1       # Automatisierter Check auf potenzielle XSS-Sinks
├── README.md           # Projektdokumentation
└── pom.xml             # Parent-POM und Maven-Modulverwaltung
```

<details>

<summary><strong>Vollständige Projektstruktur anzeigen</strong></summary>

```text
LernspielKB/
│
├── .gitignore                                           # Definiert Dateien und Ordner, die von Git ignoriert werden
├── dev.ps1                                              # PowerShell-Skript zum Bauen, Starten und Ausführen von Entwicklungstools
├── xss-check.ps1                                        # Durchsucht das Frontend nach potenziellen XSS-Sinks
├── pom.xml                                              # Parent-POM zur Verwaltung der Maven-Module
├── README.md                                            # Projektdokumentation
│
├── auth-service/                                        # Authentifizierung, Benutzer- und Klassenverwaltung
│   ├── .gitkeep
│   ├── pom.xml                                          # Maven-Konfiguration des Auth-Moduls
│   │
│   └── src/
│       └── main/
│           └── java/
│               └── de/
│                   └── lernspiel/
│                       └── auth/
│                           │
│                           ├── config/                                  # Konfiguration von Authentifizierung und Sicherheit
│                           │   ├── AdminBootstrap.java                  # Erstellt beim ersten Start automatisch einen Administrator
│                           │   ├── JwtConfig.java                       # Konfiguriert die für JWT benötigten Komponenten
│                           │   └── SecurityConfig.java                  # Definiert Zugriffsregeln und Spring-Security-Konfiguration
│                           │
│                           ├── controller/                              # REST-Schnittstellen des Auth-Moduls
│                           │   ├── DebugController.java                 # Hilfsendpunkte für lokale Entwicklung und Diagnose
│                           │   ├── SchoolClassController.java           # REST-Endpunkte für die Verwaltung von Klassen
│                           │   └── UserController.java                  # REST-Endpunkte für Login und Benutzerverwaltung
│                           │
│                           ├── dto/                                     # Request- und Response-Objekte
│                           │   ├── LoginRequest.java                    # Zugangsdaten für den Login
│                           │   ├── RegisterRequest.java                 # Gemeinsame Registrierungsdaten
│                           │   ├── SchoolClassRequest.java              # Request zum Erstellen einer Klasse
│                           │   ├── SchoolClassResponse.java             # Response einer Klasse
│                           │   ├── StudentRegisterRequest.java          # Request zum Anlegen eines Schülers
│                           │   ├── TeacherRegisterRequest.java          # Request zum Anlegen eines Lehrers
│                           │   └── UserResponse.java                    # Response-Daten eines Benutzers
│                           │
│                           ├── entity/                                  # JPA-Entitäten des Auth-Moduls
│                           │   ├── SchoolClass.java                     # Datenbankmodell einer Schulklasse
│                           │   ├── User.java                            # Datenbankmodell eines Benutzers
│                           │   └── UserType.java                        # Definiert ADMIN, TEACHER und STUDENT
│                           │
│                           ├── repository/                              # Datenbankzugriff über Spring Data JPA
│                           │   ├── SchoolClassRepository.java           # Repository für Schulklassen
│                           │   └── UserRepository.java                  # Repository für Benutzer
│                           │
│                           ├── security/                                # JWT-basierte Authentifizierungslogik
│                           │   ├── JwtAuthenticationFilter.java         # Prüft JWTs bei eingehenden Requests
│                           │   └── JwtUtils.java                        # Erstellt, liest und validiert JWTs
│                           │
│                           └── service/                                 # Geschäftslogik
│                               ├── SchoolClassService.java              # Geschäftslogik für Schulklassen
│                               └── UserService.java                     # Login und Benutzeroperationen
│
├── common/                                              # Modulübergreifend verwendete Definitionen
│   ├── .gitkeep
│   ├── pom.xml                                          # Maven-Konfiguration des Common-Moduls
│   │
│   └── src/
│       └── main/
│           └── java/
│               └── de/
│                   └── lernspiel/
│                       └── common/
│                           └── code/
│                               ├── CodeType.java                        # Zentrale Definition gemeinsam verwendeter Code-Blocktypen
│                               ├── ExecutionLog.java                    # Sammelt strukturierte Einträge einer Programmausführung
│                               ├── LogFile.java                         # Einzelner strukturierter Eintrag eines ExecutionLogs
│                               └── LogType.java                         # Definiert die unterschiedlichen Arten von Log-Einträgen
│
├── game-service/                                        # Spiellogik und eigener Code-Interpreter
│   ├── .gitkeep
│   ├── pom.xml                                          # Maven-Konfiguration des Game-Moduls
│   │
│   └── src/
│       └── main/
│           └── java/
│               └── de/
│                   └── lernspiel/
│                       └── game/
│                           │
│                           ├── controller/
│                           │   └── InterpreterController.java           # REST-Endpunkt zum Ausführen eines Programms
│                           │
│                           ├── dto/                                     # Datenmodell des visuellen Programms
│                           │   ├── CodeBlock.java                       # Basisklasse aller Code-Blöcke
│                           │   ├── ElseStatementBlock.java              # Repräsentiert einen Else-Block
│                           │   ├── IfStatementBlock.java                # Repräsentiert einen If-Block
│                           │   ├── ProgramRequest.java                  # Vom Frontend übermitteltes Gesamtprogramm
│                           │   ├── ValueBlock.java                      # Repräsentiert einen konkreten Wert
│                           │   ├── Variable.java                        # Kapselt Wert und Datentyp einer Variable
│                           │   └── VarNameBlock.java                    # Repräsentiert einen Variablennamen
│                           │
│                           └── service/
│                               └── InterpreterService.java              # Interpretiert und verarbeitet die Code-Blöcke
│
├── level-service/                                       # Levelverwaltung, Bootstrap, Skilltree-Daten, Verifikation und Lernfortschritt
│   ├── pom.xml                                          # Maven-Konfiguration des Level-Moduls
│   │
│   └── src/
│       └── main/
│           └── java/
│               └── de/
│                   └── lernspiel/
│                       └── level/
│                           │
│                           ├── config/                                  # Konfiguration und Bootstrap der Standardlevel
│                           │   ├── LevelBootstrap.java                  # Prüft und erstellt Standardlevel beim Anwendungsstart
│                           │   │
│                           │   └── bootstrap/                           # Definition der automatisch angelegten Levelgruppen
│                           │       ├── ExpectedExecutionLogs.java       # Hilfsmethoden für erwartete ExecutionLogs der Standardlevel
│                           │       ├── JavaAssignmentLevels.java        # Vordefinierte Java-Level der Kategorie ASSIGNMENTS
│                           │       ├── JavaBasicLevels.java             # Vordefinierte Java-Level der Kategorie BASICS
│                           │       ├── JavaExpressionLevels.java        # Vordefinierte Java-Level der Kategorie EXPRESSIONS
│                           │       ├── JavaVariableLevels.java          # Vordefinierte Java-Level der Kategorie VARIABLES
│                           │       └── LevelDefinitionProvider.java     # Gemeinsame Schnittstelle aller Levelgruppen
│                           │
│                           ├── controller/                              # REST-Schnittstellen der Levelverwaltung
│                           │   ├── LevelController.java                 # Erstellen, Laden und Übersicht von Leveln
│                           │   ├── LevelProgressController.java         # REST-Schnittstelle für Level-Fortschritt
│                           │   └── LevelVerificationController.java     # REST-Schnittstelle zur Prüfung einer Level-Lösung
│                           │
│                           ├── converter/                               # Konvertierung persistierter komplexer Leveldaten
│                           │   └── ExecutionLogConverter.java           # Speichert erwartete ExecutionLogs als JSON in der Datenbank
│                           │
│                           ├── dto/                                     # Request- und Response-Objekte
│                           │   ├── CreateLevelRequest.java              # Daten zum Anlegen eines Levels inklusive erwarteter Ausführung
│                           │   ├── LevelComponentRequest.java           # Komponente beim Erstellen eines Levels
│                           │   ├── LevelComponentResponse.java          # Komponente eines geladenen Levels
│                           │   ├── LevelOverviewResponse.java           # Kompakte Leveldaten für Übersichten und Skilltree
│                           │   ├── LevelProgressResponse.java           # Response für benutzerbezogenen Level-Fortschritt
│                           │   ├── LevelResponse.java                   # Vollständige Leveldaten für die Level-Seite
│                           │   └── LevelVerificationRequest.java        # Level-ID und tatsächlicher ExecutionLog für die Prüfung
│                           │
│                           ├── entity/                                  # JPA-Entitäten der Levelverwaltung
│                           │   ├── CompletedLevel.java                  # Speichert ein abgeschlossenes Level eines Benutzers
│                           │   ├── Component.java                       # Datenbankmodell eines Code-Blocktyps
│                           │   ├── Level.java                           # Datenbankmodell eines Levels inklusive erwarteter Ausführung
│                           │   ├── LevelCategory.java                   # Kategorie und Reihenfolge im Lernpfad
│                           │   ├── LevelComponent.java                  # Verknüpft Level und Component inklusive Anzahl
│                           │   └── ProgrammingLanguage.java             # Unterstützte Programmiersprache
│                           │
│                           ├── repository/                              # Datenbankzugriff über Spring Data JPA
│                           │   ├── CompletedLevelRepository.java        # Zugriff auf abgeschlossene Level
│                           │   ├── ComponentRepository.java             # Zugriff auf Code-Komponenten
│                           │   ├── LevelCategoryRepository.java         # Zugriff auf Level-Kategorien
│                           │   ├── LevelComponentRepository.java        # Zugriff auf Level-Component-Zuordnungen
│                           │   ├── LevelRepository.java                 # Zugriff auf Level
│                           │   └── ProgrammingLanguageRepository.java   # Zugriff auf Programmiersprachen
│                           │
│                           └── service/                                 # Geschäftslogik
│                               ├── LevelProgressService.java            # Geschäftslogik des Level-Fortschritts
│                               ├── LevelService.java                    # Erstellt und lädt Level und Übersichten
│                               └── LevelVerificationService.java        # Vergleicht tatsächliche und erwartete ExecutionLogs
│
└── lernspiel-app/                                       # Ausführbare Spring-Boot-Anwendung und Web-Frontend
    ├── .gitkeep
    ├── pom.xml                                          # Maven-Konfiguration der Hauptanwendung
    │
    └── src/
        └── main/
            │
            ├── java/
            │   └── de/
            │       └── lernspiel/
            │           └── LernspielApplication.java                   # Einstiegspunkt der Spring-Boot-Anwendung
            │
            └── resources/
                │
                ├── application.properties                             # Spring-Boot- und Datenbankkonfiguration
                │
                └── static/                                            # Statische Dateien des Web-Frontends
                    │
                    ├── admin.html                                      # Administratoroberfläche
                    ├── index.html                                      # Login-Seite
                    ├── level.html                                      # Dynamische Level-Seite mit Code-Editor
                    ├── sandbox.html                                    # Frei nutzbarer Code-Editor
                    ├── skilltree.html                                  # Visueller Lernpfad für Schüler
                    ├── student.html                                    # Frühere/separate Schülerseite
                    ├── teacher.html                                    # Lehreroberfläche
                    │
                    ├── css/
                    │   └── style.css                                   # Gemeinsames Styling der Webanwendung
                    │
                    └── js/
                        │
                        ├── api/                                        # Frontend-Backend-Kommunikation
                        │   ├── api.js                                  # Allgemeine Requests, JWT und Rollenlogik
                        │   ├── interpreterApi.js                       # API-Aufruf des Interpreters
                        │   ├── levelApi.js                             # API-Aufrufe für Level und Übersichten
                        │   ├── levelVerificationApi.js                 # API-Aufruf zur serverseitigen Level-Verifikation
                        │   └── progressApi.js                          # API-Aufrufe für Level-Fortschritt
                        │
                        ├── auth/                                       # Vorgesehen für gemeinsame Authentifizierungslogik
                        │
                        ├── editor/                                     # Wiederverwendbare Editor- und Palette-Komponenten
                        │   ├── blockDefinitions.js                     # Definition und Darstellung der Code-Blöcke
                        │   ├── blockFactory.js                         # Erzeugt Blockobjekte für das Programm
                        │   ├── blockInput.js                           # Eingabedialoge für konfigurierbare Code-Blöcke
                        │   ├── consoleTheme.js                         # Darstellungsmodi der Interpreter-Konsole
                        │   ├── dragDrop.js                             # Drag & Drop, Verschieben und Löschen
                        │   ├── editorState.js                          # Zustand des visuellen Programms
                        │   ├── paletteBuilder.js                       # Ergänzt Distraktoren und mischt die Level-Palette
                        │   └── renderer.js                             # Rendert den Editor-State
                        │
                        ├── navigation/
                        │   └── navigation.js                           # Gemeinsame Navigation und Logout
                        │
                        ├── pages/                                      # Seitenspezifische JavaScript-Einstiegspunkte
                        │   ├── admin.js                                # Logik der Administrator-Seite
                        │   ├── level.js                                # Dynamisches Level, Verifikation und Fortschritt
                        │   ├── login.js                                # Login und Rollenweiterleitung
                        │   ├── sandbox.js                              # Sandbox, Editor und Interpreter
                        │   ├── skilltree.js                            # Lädt und steuert den Skilltree
                        │   ├── student.js                              # Logik der separaten Schülerseite
                        │   └── teacher.js                              # Logik der Lehrer-Seite
                        │
                        └── skilltree/                                  # Wiederverwendbare Skilltree-Logik
                            ├── skilltreePhysics.js                      # Physik und leichte Node-Bewegungen beim Laden
                            ├── skilltreeRenderer.js                     # Rendert Kategorien, Level und Verbindungen
                            └── skilltreeState.js                        # Verwaltet den Zustand des Skilltrees
```

> **Hinweis:** Generierte Build-Artefakte (`target/`) sowie lokale Entwicklungsdateien und Tool-Caches sind in dieser Übersicht bewusst nicht aufgeführt.

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

Der Level-Service stellt Schnittstellen für vollständige Leveldaten sowie kompakte Levelübersichten bereit.

Vollständige Leveldaten werden von der eigentlichen Level-Seite verwendet.

Kompakte Übersichtsobjekte werden insbesondere für den Skilltree verwendet und enthalten nur die dort benötigten Informationen.

Ein vollständiges Level kann anhand seiner ID geladen werden:

```text
GET /api/levels/{levelID}
```

Der Endpunkt liefert unter anderem:

- Level-ID
- Levelname
- Beschreibung
- Kategorie
- Levelnummer
- Programmiersprache
- verfügbare Code-Komponenten
- konfigurierte Anzahl der Komponenten

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
      "type": "VAR\_NAME",
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

Über:

```text
POST /api/levels
```

können neue Level weiterhin manuell angelegt werden.

Die mitgelieferten Standardlevel werden dagegen beim Anwendungsstart automatisch über den `LevelBootstrap` geprüft und bei Bedarf erstellt.

Beispiel für das manuelle Anlegen eines Levels:

```json
{
  "levelName": "Erste Variable",
  "levelDescription": "Erstelle eine int-Variable x mit dem Wert 5.",
  "category": "BASICS",
  "categoryOrder": 1,
  "levelNumber": 1,
  "language": "JAVA",
  "components": [
    {
      "type": "INT",
      "amount": 1
    },
    {
      "type": "VAR\_NAME",
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

Für Übersichten stellt der Level-Service zusätzlich kompakte Leveldaten bereit.

Ein `LevelOverviewResponse` besitzt beispielsweise folgende Struktur:

```json
{
  "levelID": 1,
  "levelName": "Erste Variable",
  "categoryID": 1,
  "category": "BASICS",
  "categoryOrder": 1,
  "levelNumber": 1,
  "languageID": 1,
  "language": "JAVA"
}
```

Diese Daten reichen aus, um den Skilltree aufzubauen, ohne die vollständigen Component-Daten jedes Levels laden zu müssen.

</details>

<details>

<summary><strong>Level-Fortschritt anzeigen</strong></summary>

### Level-Fortschritt

Der Level-Service besitzt eine separate Progress-Schicht für benutzerbezogenen Lernfortschritt.

Die Daten werden über:

```text
LevelProgressController
```

verarbeitet und durch:

```text
LevelProgressService
```

in Verbindung mit `CompletedLevel` und `CompletedLevelRepository` gespeichert bzw. geladen.

Das Frontend kapselt die entsprechenden Requests in:

```text
progressApi.js
```

Der Skilltree verwendet diese Daten, um bereits abgeschlossene Level zu erkennen und visuell darzustellen.

Die wichtigsten Progress-Endpunkte sind:

| Methode | Endpunkt | Beschreibung |
| ------- | -------- | ------------ |
| `GET` | `/api/progress/completed-levels` | Lädt die abgeschlossenen Level des aktuell authentifizierten Benutzers |
| `POST` | `/api/progress/levels/{levelID}/complete` | Markiert ein Level als abgeschlossen |

Die Progress-Schnittstelle bildet aktuell die technische Grundlage für:

- Speichern abgeschlossener Level
- Laden des benutzerbezogenen Fortschritts
- Markieren abgeschlossener Level im Skilltree

Eine fachliche Freischaltlogik für nachfolgende Level ist davon bewusst getrennt und aktuell noch nicht endgültig definiert.

</details>

<details>

<summary><strong>Level-Verifikation anzeigen</strong></summary>

### Level-Verifikation

| Methode | Endpunkt | Beschreibung |
| ------- | -------- | ------------ |
| `POST` | `/api/levelVerification/verify` | Vergleicht den tatsächlichen ExecutionLog mit der serverseitig gespeicherten erwarteten Ausführung des Levels |

Das Frontend sendet ausschließlich die Level-ID und den tatsächlichen `ExecutionLog`. Der erwartete ExecutionLog wird serverseitig anhand der Level-ID geladen. Die Response ist ein Boolean.

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
      "type": "VAR\_NAME",
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

Die Antwort besteht aus einem strukturierten `ExecutionLog`.

Beispiel:

```json
{
  "entries": [
    {
      "logType": "PROGRAM_START",
      "data": {}
    },
    {
      "logType": "VARIABLE_DECLARATION_ASSIGNMENT",
      "data": {
        "variableType": "INT",
        "variableName": "x",
        "variableValue": 8
      }
    }
  ]
}
```

</details>

<details>

<summary><strong>Debug-Endpunkte anzeigen</strong></summary>

### Debug-Endpunkte

Die folgenden Endpunkte dienen ausschließlich der lokalen Entwicklung und Diagnose.

Sie sind nicht für den produktiven Betrieb vorgesehen.

| Methode | Endpunkt | Beschreibung |
| ------- | -------- | ------------ |
| `GET` | `/debug/db-info` | Zeigt Informationen über die aktuell verbundene Datenbank |
| `GET` | `/debug/create-test-user` | Erstellt einen Testbenutzer |
| `GET` | `/debug/list-users` | Gibt alle gespeicherten Benutzer zurück |
| `DELETE` | `/debug/drop-user` | Löscht für einen lokalen Datenbank-Reset die Tabellen `school\_class` und `user` |

> **Achtung:** Die Debug-Endpunkte dienen ausschließlich der lokalen Entwicklung und sollten später entfernt bzw. außerhalb einer Entwicklungsumgebung nicht verfügbar gemacht werden.

</details>

---

# Entwicklung

Für die alltägliche Entwicklung kann das im Projekt enthaltene PowerShell-Skript `dev.ps1` verwendet werden.

Die Einrichtung und Verwendung des Skripts ist unter [Projekt starten](#projekt-starten) beschrieben.

Neben dem Bauen und Starten der Anwendung kann darüber auch der lokale XSS Security Check ausgeführt werden.

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

---

## XSS Security Check

Für die lokale Entwicklung steht zusätzlich das Skript:

```text
xss-check.ps1
```

zur Verfügung.

Es kann direkt ausgeführt werden:

```powershell
.\xss-check.ps1
```

oder über:

```powershell
.\dev.ps1
```

und anschließend:

```text
[4] XSS Security Check
```

Der Check durchsucht das statische Frontend rekursiv nach JavaScript- und HTML-Konstrukten, die bei unsicherer Verwendung Cross-Site-Scripting ermöglichen können.

Aktuell werden unter anderem folgende Muster geprüft:

```text
innerHTML
insertAdjacentHTML(...)
document.write(...)
eval(...)
new Function(...)
```

Werden keine Stellen für ein Muster gefunden, wird dieses mit `[OK]` ausgegeben.

Gefundene potenziell relevante Stellen werden mit `[REVIEW]` markiert und inklusive Datei, Zeilennummer und Codezeile im Terminal ausgegeben.

Beispiel:

```text
[REVIEW] innerHTML
lernspiel-app\src\main\resources\static\js\pages\admin.js:128
  row\.innerHTML = `
```

Ein Treffer bedeutet ausdrücklich **nicht automatisch, dass eine XSS-Sicherheitslücke vorhanden ist**.

Beispielsweise ist:

```javascript
element.innerHTML = "";
```

grundsätzlich nur das Leeren eines Elements.

Dagegen müssen Stellen genauer geprüft werden, an denen externe oder vom Benutzer beeinflussbare Daten über `innerHTML` in das DOM eingesetzt werden.

Der Check dient deshalb als automatisierte Entwicklungshilfe, um potenziell relevante Codebereiche schnell zu finden. Er ersetzt keine vollständige Sicherheitsprüfung der Anwendung.

---

## Aktueller Entwicklungsstand

Die grundlegende Architektur des Projekts ist vorhanden.

Aktuell umgesetzt sind unter anderem:

### Authentifizierung und Benutzerverwaltung

- JWT-basierte Authentifizierung
- Login
- rollenbasierte Zugriffskontrolle
- Administratoren
- Lehrer
- Schüler
- Klassenverwaltung
- automatische Erstellung eines Administrators bei einer leeren Datenbank
- rollenabhängige Weiterleitung nach dem Login

### Projektarchitektur

- Maven-Multi-Module-Struktur
- `auth-service`
- `common`
- `game-service`
- `level-service`
- `lernspiel-app`
- `common` für modulübergreifend verwendete Definitionen
- zentraler `CodeType` für gemeinsam verwendete Code-Blocktypen
- gemeinsame `ExecutionLog`-, `LogFile`- und `LogType`-Definitionen

### Interpreter

- eigener Interpreter
- polymorphe `CodeBlock`-DTOs
- Verarbeitung von Programmen aus visuellen Code-Blöcken
- `ExecutionLog`
- Rückgabe von Interpreter-Meldungen an das Frontend

### Visueller Code-Editor

- Drag & Drop
- mehrzeilige Programme
- Verschieben von Blöcken
- Einfügen zwischen vorhandenen Blöcken
- Löschen über Drop-Zone
- modularisierte Editor-Komponenten
- wiederverwendbarer Editor-State
- wiederverwendbares Rendering
- gemeinsame Blockdefinitionen
- Interpreter-Ausgabe im Frontend
- Darstellung von Interpreterfehlern
- mehrere Darstellungsmodi der Interpreter-Konsole

### Sandbox

- frei nutzbarer visueller Code-Editor
- direkte Ausführung über den Interpreter
- unabhängige Test- und Demonstrationsumgebung
- über die Schülernavigation erreichbar

### Dynamische Level

- `level-service`
- JPA-Entity `Level`
- JPA-Entity `ProgrammingLanguage`
- JPA-Entity `Component`
- JPA-Entity `LevelComponent`
- JPA-Entity `LevelCategory`
- Zuordnung von Components und Mengen zu Leveln
- dynamische Level-Seite
- Laden vollständiger Levelinformationen aus der Datenbank
- dynamischer Aufbau der Block-Palette
- automatische Ergänzung von zwei bis drei zufälligen zusätzlichen Blocktypen
- Ausschluss bereits benötigter Blocktypen bei der Auswahl der zusätzlichen Blöcke
- zufälliges Mischen der vollständigen Block-Palette
- getrennte Palette-Logik über `paletteBuilder.js`
- Wiederverwendung der Sandbox-Editor-Module
- Übergabe von Level- und Sprachinformationen an den Interpreter
- kompakte Levelübersichten über `LevelOverviewResponse`

### Level-Bootstrap

- automatisches Prüfen der Standardlevel beim Start
- automatisches Anlegen fehlender Standardlevel
- keine erneute Anlage bereits vorhandener Level
- `LevelBootstrap` als zentraler Bootstrap
- `LevelDefinitionProvider` als gemeinsame Schnittstelle
- getrennte Definition von Levelgruppen
- `JavaBasicLevels`
- `JavaVariableLevels`
- `JavaExpressionLevels`
- `JavaAssignmentLevels`
- `ExpectedExecutionLogs` für erwartete Ausführungen
- automatische Erkennung aller Provider durch Spring
- keine manuelle Registrierung neuer Provider im `LevelBootstrap`
- Terminal-Ausgabe über neu angelegte bzw. bereits vorhandene Level

### Skilltree

- `skilltree.html` als zentrale Schüleroberfläche
- direkter Redirect von Schülern zum Skilltree
- Programmiersprachenauswahl
- dynamisches Laden vorhandener Level
- Gruppierung nach Kategorien
- Sortierung nach `categoryOrder`
- Sortierung der Level nach `levelNumber`
- Kategorie-Nodes
- Level-Nodes
- SVG-Verbindungen
- vertikaler Lernpfad
- verteilte Level-Nodes um Kategorien
- leichte Physics-Simulation
- gegenseitige Abstoßung der Level-Nodes
- Berücksichtigung der Kategorien als statische Hindernisse
- Hover-Bewegung der Level-Nodes
- Aktualisierung der Verbindungslinien während der Bewegung
- visuelle Darstellung abgeschlossener Level

### Level-Verifikation

- erwarteter `ExecutionLog` als Bestandteil eines Levels
- JSON-Persistierung über `ExecutionLogConverter`
- `LevelVerificationController`
- `LevelVerificationService`
- `LevelVerificationRequest`
- `levelVerificationApi.js`
- serverseitiger Vergleich von tatsächlicher und erwarteter Ausführung
- erwartete Lösung wird nicht an das Schüler-Frontend ausgeliefert

### Level-Fortschritt

- `CompletedLevel`
- `CompletedLevelRepository`
- `LevelProgressService`
- `LevelProgressController`
- `LevelProgressResponse`
- `progressApi.js`
- benutzerbezogenes Laden abgeschlossener Level
- Integration des Fortschritts in den Skilltree
- automatische Speicherung nach erfolgreicher Level-Verifikation

### Navigation

- gemeinsame Navigation über `navigation.js`
- Lernpfad als zentrale Schülerseite
- Navigation vom Skilltree zur Sandbox
- Navigation vom Level zurück zum Lernpfad
- Navigation vom Level zur Sandbox
- Navigation von der Sandbox zurück zum Lernpfad
- gemeinsamer Logout

### Entwicklungstools

- `dev.ps1` für Build und Start
- integrierter Menüpunkt für den XSS Security Check
- separates `xss-check.ps1`
- rekursive Prüfung des statischen Frontends auf potenzielle XSS-Sinks
- Ausgabe der Fundstellen inklusive Datei und Zeilennummer

Der aktuelle Schülerfluss ist damit grundsätzlich vollständig navigierbar:

```text
Login
  ↓
Skilltree
  ├────────────→ Sandbox
  │                 │
  │                 └────→ Skilltree
  │
  └────→ Level
           │
           ├────────→ Sandbox
           │
           └────────→ Skilltree
```

---

## Noch offene Punkte

Einige Funktionen sind bewusst noch nicht abschließend umgesetzt oder fachlich noch nicht festgelegt.

### Begrenzung verfügbarer Blöcke

Die für die Lösung vorgesehenen Komponenten und ihre Mengen werden über `LevelComponent.amount` gespeichert und an das Frontend übertragen.

Zusätzlich ergänzt `paletteBuilder.js` ausschließlich für die Darstellung zwei bis drei zufällige zusätzliche Blocktypen als Distraktoren.

Diese zusätzlichen Blöcke verändern die gespeicherte Leveldefinition nicht.

Die tatsächliche Begrenzung der maximal verwendbaren Anzahl eines Blocktyps im Editor ist weiterhin noch nicht vollständig umgesetzt.

### Freischaltung von Leveln

Die Speicherung abgeschlossener Level ist bereits vorhanden.

Noch nicht endgültig entschieden ist, ob und wie zukünftige Level gesperrt werden.

Denkbare Varianten sind beispielsweise:

- alle Level sind jederzeit verfügbar
- Kategorien werden schrittweise freigeschaltet
- einzelne Level werden anhand vorheriger Level freigeschaltet
- Fortschritt wird ausschließlich visuell dargestellt

Eine feste `requires`-Beziehung wurde deshalb bisher bewusst nicht eingeführt.

### Skilltree-Progress

Die Verbindungen zwischen Kategorien können später optional zur Darstellung des Lernfortschritts verwendet werden.

Beispielsweise könnte eine Verbindung abhängig vom Anteil abgeschlossener Level einer Kategorie zunehmend eingefärbt werden.

Diese Darstellung ist aktuell bewusst noch nicht implementiert, da sie von der endgültigen Progress- und Unlock-Logik abhängt.

### XSS-Review

Der automatisierte XSS Security Check kann potenziell relevante Stellen im Frontend erkennen und ausgeben.

Die aktuell gefundenen `innerHTML`-Verwendungen müssen noch einzeln darauf geprüft werden, ob dort vom Benutzer oder über APIs gelieferte Daten unsicher in das DOM eingesetzt werden.

Der Scanner dient dabei als Unterstützung für die manuelle Prüfung und nicht als automatischer Nachweis, dass eine Stelle sicher oder unsicher ist.

### Visueller Feinschliff

Der Skilltree ist funktional und besitzt bereits eine grundlegende visuelle Darstellung inklusive Physics und Hover-Effekten.

Weitere Anpassungen können später unter anderem betreffen:

- Abstände
- Node-Größen
- Farben
- Hover-Informationen
- zusätzliche Levelinformationen
- Darstellung des Fortschritts

Diese Punkte betreffen hauptsächlich den visuellen Feinschliff und verändern die grundlegende Skilltree-Architektur nicht.

### Aufräumen vor Projektabschluss

Vor dem endgültigen Projektabschluss sollten Entwicklungs- und Hilfskomponenten noch überprüft werden.

Dazu gehören insbesondere:

- Debug-Endpunkte entfernen oder absichern
- nicht mehr benötigte Seiten überprüfen
- Secrets und DB-Config aus application.properties löschen, rerollen und in Umgebungsvariable anlegen
- Testdaten bereinigen
- XSS-Fundstellen überprüfen
- finale Security-Konfiguration prüfen
- README auf den finalen Projektstand bringen
