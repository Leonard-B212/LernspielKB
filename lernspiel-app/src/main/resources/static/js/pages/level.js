/**
 * Einstiegspunkt der Level-Seite.
 *
 * Die Datei lädt das gewünschte Level aus dem Backend,
 * erzeugt daraus die Block-Palette und verbindet anschließend
 * dieselben Editor-Module, die auch von der Sandbox verwendet werden.
 */

import { getLevel } from "../api/levelApi.js";
import { runProgram } from "../api/interpreterApi.js";
import { verifyLevel } from "../api/levelVerificationApi.js";
import { BLOCK_DEFINITIONS } from "../editor/blockDefinitions.js";
import { buildLevelPalette } from "../editor/paletteBuilder.js";
import { createEditorState } from "../editor/editorState.js";
import { createProgramRenderer } from "../editor/renderer.js";
import { createDragDropController } from "../editor/dragDrop.js";
import { initializeConsoleTheme } from "../editor/consoleTheme.js";
import { completeLevel } from "../api/progressApi.js";

/* =========================================================
   DOM
   ========================================================= */

const levelTitle = document.getElementById("level-title");
const levelDescription = document.getElementById("level-description");
const levelCategory = document.getElementById("level-category");
const levelLanguage = document.getElementById("level-language");
const palette = document.getElementById("block-palette");
const programDropzone = document.getElementById("program-dropzone");
const trashDropzone = document.getElementById("trash-dropzone");
const resetButton = document.getElementById("reset-button");
const runButton = document.getElementById("run-button");
const message = document.getElementById("level-message");
const interpreterOutput = document.getElementById("interpreter-output");
const interpreterConsole = document.getElementById("interpreter-console");
const consoleThemeSwitch = document.getElementById("console-theme-switch");
const levelNumber = document.getElementById("level-number");

/* =========================================================
   LEVEL
   ========================================================= */

let loadedLevel = null;

// Liest die Level-ID aus ?id=... der aktuellen URL.
function getLevelIDFromUrl() {
    const params = new URLSearchParams(window.location.search);
    const levelID = Number(params.get("id"));

    if (!Number.isInteger(levelID) || levelID <= 0) {
        throw new Error("Keine gültige Level-ID angegeben.");
    }

    return levelID;
}

// Lädt das Level aus dem Backend und zeigt dessen Metadaten an.
async function loadLevel() {
    const levelID = getLevelIDFromUrl();

    loadedLevel = await getLevel(levelID);

    levelNumber.textContent = loadedLevel.levelNumber;
    levelTitle.textContent = loadedLevel.levelName;
    levelDescription.textContent = loadedLevel.levelDescription;
    levelCategory.textContent = loadedLevel.category;
    levelLanguage.textContent = loadedLevel.language;

    const paletteComponents = buildLevelPalette(loadedLevel.components);
    renderPalette(paletteComponents);
}

// Erzeugt die Block-Palette anhand der Components des geladenen Levels.
function renderPalette(components) {
    palette.innerHTML = "";

    components.forEach((component) => {
        const definition = BLOCK_DEFINITIONS[component.type];

        if (!definition) {
            console.warn("Unbekannter CodeType:", component.type);
            return;
        }

        const block = document.createElement("div");

        block.classList.add("code-block", definition.cssClass);
        block.draggable = true;
        block.dataset.type = definition.type;
        block.textContent = definition.label;
        block.dataset.amount = component.amount;

        palette.appendChild(block);
    });
}

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

// Rendert den aktuellen Editor-State.
function renderProgram() {
    renderer.render(editorState.getProgram());
}

// Erstellt die Drag-&-Drop-Steuerung für das Level.
function initializeEditor() {
    dragDropController = createDragDropController({
        palette,
        programDropzone,
        trashDropzone,
        editorState,
        renderProgram,
        showError: (text) => showMessage(text, true)
    });

    dragDropController.initialize();
    renderProgram();
}

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

// Löscht das aktuell gebaute Programm.
resetButton.addEventListener("click", () => {
    editorState.clear();
    renderProgram();
    showMessage("");
    resetInterpreterOutput();
});

/* =========================================================
   AUSFÜHREN
   ========================================================= */

// Führt das Programm aus und prüft das Ergebnis anschließend gegen das Level.
runButton.addEventListener("click", async () => {
    const program = editorState.getProgram();

    if (program.length === 0) {
        showMessage("Das Programm enthält noch keine Blöcke.", true);
        return;
    }

    if (!loadedLevel) {
        showMessage("Das Level wurde noch nicht geladen.", true);
        return;
    }

    const programRequest = {
        userId: 1,
        levelId: loadedLevel.levelID,
        languageId: loadedLevel.languageID,
        program
    };

    try {
        const output = await runProgram(programRequest);

        renderInterpreterOutput(output);

        const hasInterpreterError = output.entries?.some(
            (entry) => entry.logType === "ERROR"
        ) ?? false;

        if (hasInterpreterError) {
            showMessage(
                "Der Interpreter hat einen Fehler im Programm gefunden.",
                true
            );
            return;
        }

        const successful = await verifyLevel(
            loadedLevel.levelID,
            output
        );

        if (successful) {
            await completeLevel(loadedLevel.levelID);
            showMessage("Level erfolgreich abgeschlossen.");
        } else {
            showMessage(
                "Die Lösung erfüllt die Anforderungen des Levels noch nicht.",
                true
            );
        }
    } catch (error) {
        console.error("Fehler beim Ausführen oder Prüfen des Levels:", error);

        showMessage(
            error.message || "Programm konnte nicht ausgeführt oder geprüft werden.",
            true
        );
    }
});

/* =========================================================
   INTERPRETER-AUSGABE
   ========================================================= */

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

// Setzt die Interpreter-Ausgabe auf ihren Ausgangszustand zurück.
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

// Zeigt eine Status- oder Fehlermeldung auf der Level-Seite an.
function showMessage(text, isError = false) {
    message.textContent = text;
    message.classList.toggle("error", isError);
    message.classList.toggle("success", !isError && Boolean(text));
}

/* =========================================================
   INITIALISIERUNG
   ========================================================= */

// Lädt zuerst das Level und initialisiert anschließend den Editor.
async function initializeLevelPage() {
    try {
        await loadLevel();
        initializeEditor();
    } catch (error) {
        console.error("Level konnte nicht geladen werden:", error);

        showMessage(
            error.message || "Level konnte nicht geladen werden.",
            true
        );

        levelTitle.textContent = "Level konnte nicht geladen werden";
        runButton.disabled = true;
    }
}

initializeLevelPage();