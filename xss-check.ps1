# -------------------------------------------------------------------
# Lernspiel XSS Security Check
# -------------------------------------------------------------------
#
# Durchsucht das Frontend nach JavaScript- und HTML-Konstrukten,
# die bei unsicherer Verwendung Cross-Site-Scripting ermöglichen
# können.
#
# Ein Treffer bedeutet NICHT automatisch, dass eine XSS-Lücke
# vorhanden ist. Die entsprechende Stelle muss manuell geprüft werden.
# -------------------------------------------------------------------

$ErrorActionPreference = "Stop"

$ProjectRoot = $PSScriptRoot

$StaticRoot = Join-Path `
    $ProjectRoot `
    "lernspiel-app\src\main\resources\static"


# -------------------------------------------------------------------
# Potenziell gefährliche Muster
# -------------------------------------------------------------------

$Patterns = @(
    @{
        Name = "innerHTML"
        Pattern = "\.innerHTML"
    },
    @{
        Name = "insertAdjacentHTML"
        Pattern = "insertAdjacentHTML\s*\("
    },
    @{
        Name = "document.write"
        Pattern = "document\.write\s*\("
    },
    @{
        Name = "eval"
        Pattern = "\beval\s*\("
    },
    @{
        Name = "new Function"
        Pattern = "new\s+Function\s*\("
    }
)


# -------------------------------------------------------------------
# Projekt prüfen
# -------------------------------------------------------------------

if (-not (Test-Path $StaticRoot)) {
    throw "Frontend-Verzeichnis wurde nicht gefunden: $StaticRoot"
}


Write-Host ""
Write-Host "==========================================" -ForegroundColor DarkCyan
Write-Host "          XSS Security Check"              -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor DarkCyan
Write-Host ""

Write-Host "Durchsuche:" -ForegroundColor Gray
Write-Host $StaticRoot
Write-Host ""


# -------------------------------------------------------------------
# Dateien laden
# -------------------------------------------------------------------

$Files = Get-ChildItem `
    -Path $StaticRoot `
    -Recurse `
    -File |
    Where-Object {
        $_.Extension -in @(
            ".js",
            ".html"
        )
    }


$TotalFindings = 0


# -------------------------------------------------------------------
# Muster durchsuchen
# -------------------------------------------------------------------

foreach ($PatternDefinition in $Patterns) {

    $PatternFindings = 0

    foreach ($File in $Files) {

        $Matches = Select-String `
            -Path $File.FullName `
            -Pattern $PatternDefinition.Pattern


        foreach ($Match in $Matches) {

            if ($PatternFindings -eq 0) {

                Write-Host ""
                Write-Host "[REVIEW] $($PatternDefinition.Name)" `
                    -ForegroundColor Yellow
            }


            $RelativePath =
                $File.FullName.Substring(
                    $ProjectRoot.Length + 1
                )


            # Format:
            #
            # datei.js:42
            #
            # wird von VS Code-Terminals normalerweise als
            # navigierbare Fundstelle erkannt.
            Write-Host ""
            Write-Host "$RelativePath`:$($Match.LineNumber)" `
                -ForegroundColor Cyan

            Write-Host "  $($Match.Line.Trim())"


            $PatternFindings++
            $TotalFindings++
        }
    }


    if ($PatternFindings -eq 0) {

        Write-Host "[OK] $($PatternDefinition.Name)" `
            -ForegroundColor Green
    }
}


# -------------------------------------------------------------------
# Zusammenfassung
# -------------------------------------------------------------------

Write-Host ""
Write-Host "------------------------------------------"


if ($TotalFindings -eq 0) {

    Write-Host ""
    Write-Host "Keine potenziellen XSS-Sinks gefunden." `
        -ForegroundColor Green

} else {

    Write-Host ""
    Write-Host "$TotalFindings potenzielle XSS-Stelle(n) gefunden." `
        -ForegroundColor Yellow

    Write-Host ""
    Write-Host "Ein Treffer bedeutet nicht automatisch eine Sicherheitslücke."
    Write-Host "Prüfe insbesondere, ob Daten aus Benutzereingaben oder APIs"
    Write-Host "ungefiltert in die jeweilige Stelle eingesetzt werden."
}


Write-Host ""