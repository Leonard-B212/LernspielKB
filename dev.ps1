# Stoppt das Skript bei Fehlern, statt einfach weiterzumachen.
$ErrorActionPreference = "Stop"

# -------------------------------------------------------------------
# Projektpfade
# -------------------------------------------------------------------

# Root-Verzeichnis: der Ordner, in dem dieses Skript liegt.
$ProjectRoot = $PSScriptRoot

# Maven-POM des gesamten Multi-Modul-Projekts.
$RootPom = Join-Path $ProjectRoot "pom.xml"

# Maven-POM der startbaren Spring-Boot-Anwendung.
$AppPom = Join-Path $ProjectRoot "lernspiel-app\pom.xml"

# Maven-Kommando. Kann später z. B. auf ".\mvnw.cmd" geändert werden.
$MavenCommand = "mvn"


# -------------------------------------------------------------------
# Hilfsfunktionen
# -------------------------------------------------------------------

function Test-ProjectSetup {
    if (-not (Test-Path $RootPom)) {
        throw "Root-POM wurde nicht gefunden: $RootPom"
    }

    if (-not (Test-Path $AppPom)) {
        throw "App-POM wurde nicht gefunden: $AppPom"
    }

    if (-not (Get-Command $MavenCommand -ErrorAction SilentlyContinue)) {
        throw "Maven wurde nicht gefunden. Prüfe, ob 'mvn' im PATH eingetragen ist."
    }
}


function Invoke-CleanInstall {
    Write-Host ""
    Write-Host "Starte Maven Clean Install..." -ForegroundColor Cyan
    Write-Host "Projekt: $ProjectRoot"
    Write-Host ""

    & $MavenCommand `
        --file $RootPom `
        clean install

    if ($LASTEXITCODE -ne 0) {
        throw "Maven Clean Install ist fehlgeschlagen."
    }

    Write-Host ""
    Write-Host "Clean Install erfolgreich abgeschlossen." -ForegroundColor Green
}


function Start-Lernspiel {
    Write-Host ""
    Write-Host "Starte die Lernspiel-Anwendung..." -ForegroundColor Cyan
    Write-Host "Modul: lernspiel-app"
    Write-Host ""

    & $MavenCommand `
        --file $AppPom `
        spring-boot:run

    if ($LASTEXITCODE -ne 0) {
        throw "Die Lernspiel-Anwendung konnte nicht gestartet werden."
    }
}


function Show-Menu {
    Clear-Host

    Write-Host "==========================================" -ForegroundColor DarkCyan
    Write-Host "          Lernspiel Entwicklung"           -ForegroundColor Cyan
    Write-Host "==========================================" -ForegroundColor DarkCyan
    Write-Host ""
    Write-Host "[1] Clean Install"
    Write-Host "[2] Clean Install und Start"
    Write-Host "[3] Nur Start"
    Write-Host "[0] Beenden"
    Write-Host ""
}


# -------------------------------------------------------------------
# Hauptprogramm
# -------------------------------------------------------------------

try {
    Test-ProjectSetup

    Show-Menu
    $Selection = Read-Host "Bitte Auswahl eingeben"

    switch ($Selection) {
        "1" {
            Invoke-CleanInstall
        }

        "2" {
            Invoke-CleanInstall
            Start-Lernspiel
        }

        "3" {
            Start-Lernspiel
        }

        "0" {
            Write-Host "Skript beendet."
            exit 0
        }

        default {
            Write-Host ""
            Write-Host "Ungültige Auswahl. Bitte 0, 1, 2 oder 3 eingeben." `
                -ForegroundColor Yellow
            exit 1
        }
    }
}
catch {
    Write-Host ""
    Write-Host "Fehler: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}