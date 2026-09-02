/**
 * Einstiegspunkt der Sandbox-Seite.
 *
 * Diese Datei verbindet die einzelnen wiederverwendbaren Module:
 *
 * - EditorState verwaltet das Programm
 * - Renderer zeichnet das Programm
 * - DragDropController verarbeitet Benutzerinteraktionen
 * - InterpreterApi sendet das Programm an das Backend
 * - ConsoleTheme steuert die Darstellung der Interpreter-Konsole
 *
 * Die eigentliche Editor-Logik liegt bewusst nicht in dieser Datei,
 * damit sie später auch auf einer Level-Seite wiederverwendet werden kann.
 */

import { createEditorState } from "../editor/editorState.js";
import { createProgramRenderer } from "../editor/renderer.js";
import { createDragDropController } from "../editor/dragDrop.js";
import { initializeConsoleTheme } from "../editor/consoleTheme.js";
import { runProgram } from "../api/interpreterApi.js";

/* =========================================================
   DOM
   ========================================================= */

const palette = document.getElementById("block-palette");
const programDropzone = document.getElementById("program-dropzone");
const trashDropzone = document.getElementById("trash-dropzone");
const resetButton = document.getElementById("reset-button");
const runButton = document.getElementById("run-button");
const message = document.getElementById("sandbox-message");
const interpreterOutput = document.getElementById("interpreter-output");
const interpreterConsole = document.getElementById("interpreter-console");
const consoleThemeSwitch = document.getElementById("console-theme-switch");

/* =========================================================
   EDITOR
   ========================================================= */

const editorState = createEditorState();

let renderer;
let dragDropController;

renderer = createProgramRenderer({
    programDropzone,
    onBlockDragStart: (...args) =>
        dragDropController.handleProgramBlockDragStart(...args),
    onBlockDragEnd: (...args) =>
        dragDropController.handleProgramBlockDragEnd(...args)
});

// Rendert den aktuellen Editor-State in der Programmfläche.
function renderProgram() {
    renderer.render(editorState.getProgram());
}

dragDropController = createDragDropController({
    palette,
    programDropzone,
    trashDropzone,
    editorState,
    renderProgram,
    showError: (text) => showMessage(text, true)
});

dragDropController.initialize();

/* =========================================================
   CONSOLE THEME
   ========================================================= */

initializeConsoleTheme({
    interpreterConsole,
    consoleThemeSwitch
});

/* =========================================================
   RESET
   ========================================================= */

// Löscht Programm, Statusmeldung und Interpreter-Ausgabe.
resetButton.addEventListener("click", () => {
    editorState.clear();
    renderProgram();
    showMessage("");
    resetInterpreterOutput();
});

/* =========================================================
   AUSFÜHREN
   ========================================================= */

// Erstellt aus dem aktuellen Editor-State einen ProgramRequest, sendet ihn an den Interpreter und zeigt dessen Antwort an.
runButton.addEventListener("click", async () => {
    const program = editorState.getProgram();

    if (program.length === 0) {
        showMessage("Das Programm enthält noch keine Blöcke.", true);
        return;
    }

    /*
     * Die Sandbox verwendet aktuell feste Testwerte für Benutzer, Level und Sprache.
     */
    const programRequest = {
        userId: 1,
        levelId: 1,
        languageId: 1,
        program
    };

    console.log("ProgramRequest an Interpreter:", programRequest);

    try {
        const output = await runProgram(programRequest);

        console.log("Antwort vom Interpreter:", output);

        renderInterpreterOutput(output);

        const hasInterpreterError = output.entries?.some(
            (entry) => entry.logType === "ERROR"
        ) ?? false;

        if (hasInterpreterError) {
            showMessage(
                "Der Interpreter hat einen Fehler im Programm gefunden.",
                true
            );
        } else {
            showMessage("");
        }
    } catch (error) {
        console.error("Fehler beim Interpreter-Aufruf:", error);

        showMessage(
            error.message || "Programm konnte nicht ausgeführt werden.",
            true
        );
    }
});

/* =========================================================
   INTERPRETER-AUSGABE
   ========================================================= */

// Rendert die vom Backend zurückgegebenen Interpreter-Meldungen.
// Zeigt die Einträge des vom Interpreter erzeugten ExecutionLogs an.
function renderInterpreterOutput(executionLog) {
    interpreterOutput.innerHTML = "";

    const entries = executionLog?.entries ?? [];

    if (entries.length === 0) {
        const placeholder = document.createElement("span");

        placeholder.classList.add("interpreter-output-placeholder");
        placeholder.textContent = "Der Interpreter hat keine Ausgabe erzeugt.";

        interpreterOutput.appendChild(placeholder);
        return;
    }

    entries.forEach((entry) => {
        const line = document.createElement("div");

        line.classList.add("interpreter-output-line");

        if (entry.logType === "ERROR") {
            line.classList.add("error");
        }

        const contents = Object.entries(entry.contents ?? {})
            .map(([key, value]) => `${key}: ${value}`)
            .join(", ");

        line.textContent = contents
            ? `${entry.logType}: ${contents}`
            : entry.logType;

        interpreterOutput.appendChild(line);
    });
}

// Setzt die Interpreter-Konsole auf ihren Ausgangszustand zurück.
function resetInterpreterOutput() {
    interpreterOutput.innerHTML = "";

    const placeholder = document.createElement("span");

    placeholder.classList.add("interpreter-output-placeholder");
    placeholder.textContent = "Noch kein Programm ausgeführt.";

    interpreterOutput.appendChild(placeholder);
}

/* =========================================================
   STATUSMELDUNG
   ========================================================= */

// Zeigt eine allgemeine Status- oder Fehlermeldung der Sandbox an.
function showMessage(text, isError = false) {
    message.textContent = text;
    message.classList.toggle("error", isError);
    message.classList.toggle("success", !isError && Boolean(text));
}

/* =========================================================
   INITIALISIERUNG
   ========================================================= */

renderProgram();