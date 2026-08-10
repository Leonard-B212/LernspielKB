const palette = document.getElementById("block-palette");
const programDropzone = document.getElementById("program-dropzone");
const trashDropzone = document.getElementById("trash-dropzone");

const resetButton = document.getElementById("reset-button");
const runButton = document.getElementById("run-button");

const message = document.getElementById("sandbox-message");


/*
 * Das Programm bleibt intern eine flache Liste.
 *
 * BREAK beendet eine Codezeile.
 *
 * Beispiel:
 *
 * INT x = 5 BREAK
 * x = x + 10 BREAK
 *
 * wird intern weiterhin einfach hintereinander gespeichert.
 */
let program = [];


/*
 * Informationen über den aktuell gezogenen Block.
 */
let draggedElement = null;
let draggedSource = null;


/*
 * An welcher Position würde der Block aktuell eingefügt werden?
 *
 * Beispiel:
 *
 * [int] [x] | [=] [5]
 *
 * currentDropIndex würde auf die Position vor "=" zeigen.
 */
let currentDropIndex = null;


/* =========================================================
   BLOCK-PALETTE
   ========================================================= */

palette.querySelectorAll(".code-block").forEach((block) => {

    block.addEventListener("dragstart", (event) => {

        draggedElement = block;
        draggedSource = "palette";

        event.dataTransfer.effectAllowed = "copy";

        event.dataTransfer.setData(
            "text/plain",
            block.dataset.type
        );

        programDropzone.classList.add("drag-active");
    });


    block.addEventListener("dragend", () => {

        resetDragState();
        clearDropIndicators();

    });

});


/* =========================================================
   PROGRAMM-DROPZONE
   ========================================================= */

programDropzone.addEventListener("dragover", (event) => {

    event.preventDefault();

    programDropzone.classList.add("drag-over");


    /*
     * Direkt über einem Einfüge-Indikator.
     */
    const indicator =
        event.target.closest(".drop-indicator");


    if (indicator) {

        activateDropIndicator(
            Number(indicator.dataset.insertIndex)
        );

        return;
    }


    /*
     * Wenn direkt über einem Codeblock gezogen wird,
     * bestimmen wir anhand der Mausposition,
     * ob davor oder dahinter eingefügt wird.
     */
    const block =
        event.target.closest(".program-block");


    if (block) {

        const index =
            Number(block.dataset.index);

        const rect =
            block.getBoundingClientRect();

        const mouseIsRightHalf =
            event.clientX >
            rect.left + rect.width / 2;


        const insertIndex =
            mouseIsRightHalf
                ? index + 1
                : index;


        activateDropIndicator(insertIndex);

        return;
    }


    /*
     * Wenn über eine leere Zeile gezogen wird,
     * am Ende dieser Zeile einfügen.
     */
    const line =
        event.target.closest(".code-line");


    if (line) {

        activateDropIndicator(
            Number(line.dataset.endIndex)
        );

        return;
    }


    /*
     * Fallback:
     * ganz ans Ende des Programms.
     */
    activateDropIndicator(program.length);

});


programDropzone.addEventListener("dragleave", (event) => {

    /*
     * dragleave feuert auch beim Wechsel zwischen
     * Kindern der Dropzone.
     *
     * Deshalb nur entfernen, wenn wir wirklich
     * die gesamte Programmfläche verlassen.
     */
    if (
        !programDropzone.contains(
            event.relatedTarget
        )
    ) {

        programDropzone.classList.remove("drag-over");
        clearDropIndicators();
    }

});


programDropzone.addEventListener("drop", (event) => {

    event.preventDefault();

    programDropzone.classList.remove("drag-over");


    /*
     * Falls keine bestimmte Position erkannt wurde:
     * ans Ende.
     */
    let insertIndex =
        currentDropIndex ?? program.length;


    /* =====================================================
       NEUER BLOCK AUS DER PALETTE
       ===================================================== */

    if (draggedSource === "palette") {

        const type =
            draggedElement.dataset.type;


        const blockData =
            createBlockData(type);


        /*
         * z. B. Prompt abgebrochen
         */
        if (!blockData) {

            resetDragState();
            clearDropIndicators();

            return;
        }


        program.splice(
            insertIndex,
            0,
            blockData
        );


        renderProgram();

    }


    /* =====================================================
       BESTEHENDEN BLOCK VERSCHIEBEN
       ===================================================== */

    else if (draggedSource === "program") {

        const oldIndex =
            Number(draggedElement.dataset.index);


        const movedBlock =
            program.splice(oldIndex, 1)[0];


        /*
         * Wird ein Element entfernt, verschieben sich
         * alle Indizes dahinter um eins.
         *
         * Deshalb müssen wir den Dropindex korrigieren.
         */
        if (insertIndex > oldIndex) {
            insertIndex--;
        }


        program.splice(
            insertIndex,
            0,
            movedBlock
        );


        renderProgram();

    }


    resetDragState();
    clearDropIndicators();

});


/* =========================================================
   MÜLLEIMER
   ========================================================= */

trashDropzone.addEventListener("dragover", (event) => {

    /*
     * Nur Blöcke aus dem Programm können gelöscht werden.
     */
    if (draggedSource !== "program") {
        return;
    }


    event.preventDefault();

    trashDropzone.classList.add("drag-over");

    clearDropIndicators();

});


trashDropzone.addEventListener("dragleave", () => {

    trashDropzone.classList.remove("drag-over");

});


trashDropzone.addEventListener("drop", (event) => {

    event.preventDefault();

    trashDropzone.classList.remove("drag-over");


    if (draggedSource !== "program") {

        resetDragState();

        return;
    }


    const index =
        Number(draggedElement.dataset.index);


    program.splice(index, 1);


    renderProgram();

    resetDragState();

});


/* =========================================================
   BLOCKDATEN ERZEUGEN
   ========================================================= */

function createBlockData(type) {

    switch (type) {

        case "VAR_NAME":
            return createVariableBlock();

        case "VALUE":
            return createValueBlock();

        default:
            return {
                type: type
            };

    }

}


/* =========================================================
   VARIABLE
   ========================================================= */

function createVariableBlock() {

    const name = window.prompt(
        "Wie soll die Variable heißen?"
    );


    if (name === null) {
        return null;
    }


    const cleanedName =
        name.trim();


    if (!cleanedName) {

        showMessage(
            "Der Variablenname darf nicht leer sein.",
            true
        );

        return null;

    }


    /*
     * Einfache Java-artige Prüfung.
     */
    const validName =
        /^[a-zA-Z_$][a-zA-Z0-9_$]*$/
            .test(cleanedName);


    if (!validName) {

        showMessage(
            "Ungültiger Variablenname.",
            true
        );

        return null;

    }


    return {
        type: "VAR_NAME",
        name: cleanedName
    };

}


/* =========================================================
   WERT
   ========================================================= */

function createValueBlock() {

    const typeInput = window.prompt(

        "Welchen Datentyp hat der Wert?\n\n" +
        "INT\nSTRING\nBOOLEAN",

        "INT"

    );


    if (typeInput === null) {
        return null;
    }


    const valueType =
        typeInput
            .trim()
            .toUpperCase();


    if (
        !["INT", "STRING", "BOOLEAN"]
            .includes(valueType)
    ) {

        showMessage(
            "Datentyp muss INT, STRING oder BOOLEAN sein.",
            true
        );

        return null;

    }


    const rawValue =
        window.prompt(
            `Wert für ${valueType}:`
        );


    if (rawValue === null) {
        return null;
    }


    let parsedValue;


    switch (valueType) {

        case "INT":

            parsedValue =
                Number(rawValue);


            if (
                !Number.isInteger(parsedValue)
            ) {

                showMessage(
                    "Bitte eine gültige ganze Zahl eingeben.",
                    true
                );

                return null;

            }

            break;


        case "BOOLEAN":

            if (
                rawValue.toLowerCase() !== "true" &&
                rawValue.toLowerCase() !== "false"
            ) {

                showMessage(
                    "Boolean muss true oder false sein.",
                    true
                );

                return null;

            }


            parsedValue =
                rawValue.toLowerCase() === "true";

            break;


        case "STRING":

            parsedValue = rawValue;

            break;

    }


    return {

        type: "VALUE",

        value: {
            value: parsedValue,
            type: valueType
        }

    };

}


/* =========================================================
   PROGRAMM RENDERN
   ========================================================= */

function renderProgram() {

    programDropzone.innerHTML = "";


    const lines =
        buildProgramLines();


    lines.forEach(
        (lineData, lineIndex) => {

            const line =
                document.createElement("div");


            line.classList.add("code-line");


            line.dataset.endIndex =
                lineData.endIndex;


            /* -------------------------
               Zeilennummer
               ------------------------- */

            const lineNumber =
                document.createElement("div");


            lineNumber.classList.add(
                "line-number"
            );


            lineNumber.textContent =
                lineIndex + 1;


            line.appendChild(lineNumber);


            /* -------------------------
               Inhalt der Zeile
               ------------------------- */

            const content =
                document.createElement("div");


            content.classList.add(
                "code-line-content"
            );


            /*
             * Einfügestelle am Anfang der Zeile.
             */
            content.appendChild(
                createDropIndicator(
                    lineData.startIndex
                )
            );


            lineData.blocks.forEach(
                ({ block, index }) => {

                    const element =
                        createProgramBlock(
                            block,
                            index
                        );


                    content.appendChild(
                        element
                    );


                    /*
                     * Nach jedem Block eine weitere
                     * Einfügestelle.
                     */
                    content.appendChild(
                        createDropIndicator(
                            index + 1
                        )
                    );

                }
            );


            /*
             * Leere Zeile anzeigen.
             */
            if (
                lineData.blocks.length === 0
            ) {

                const hint =
                    document.createElement("span");


                hint.classList.add(
                    "empty-line-hint"
                );


                hint.textContent =
                    lineIndex === 0
                        ? "Ziehe Code-Blöcke hier hinein"
                        : "Nächste Codezeile";


                content.appendChild(hint);

            }


            line.appendChild(content);

            programDropzone.appendChild(line);

        }
    );

}


/* =========================================================
   PROGRAMM IN ZEILEN AUFTEILEN
   ========================================================= */

/*
 * Wichtig:
 *
 * Das verändert NICHT das eigentliche program[].
 *
 * Es wird nur fürs Frontend entschieden,
 * welche Blöcke in welcher Zeile angezeigt werden.
 */
function buildProgramLines() {

    const lines = [];


    let currentLine = {
        startIndex: 0,
        blocks: []
    };


    program.forEach(
        (block, index) => {

            currentLine.blocks.push({
                block,
                index
            });


            /*
             * Semikolon / BREAK beendet die Zeile.
             */
            if (block.type === "BREAK") {

                currentLine.endIndex =
                    index + 1;


                lines.push(currentLine);


                currentLine = {

                    startIndex:
                        index + 1,

                    blocks: []

                };

            }

        }
    );


    /*
     * Noch nicht abgeschlossene Zeile.
     */
    if (currentLine.blocks.length > 0) {

        currentLine.endIndex =
            program.length;


        lines.push(currentLine);

    }


    /*
     * Komplett leeres Programm.
     */
    if (program.length === 0) {

        lines.push({

            startIndex: 0,
            endIndex: 0,
            blocks: []

        });

    }


    /*
     * Wenn die letzte Zeile mit ";" abgeschlossen wurde,
     * automatisch eine neue leere Codezeile anzeigen.
     */
    else if (
        program[program.length - 1].type === "BREAK"
    ) {

        lines.push({

            startIndex: program.length,
            endIndex: program.length,
            blocks: []

        });

    }


    return lines;

}


/* =========================================================
   PROGRAMMBLOCK ERZEUGEN
   ========================================================= */

function createProgramBlock(
    blockData,
    index
) {

    const element =
        document.createElement("div");


    element.classList.add(
        "code-block",
        "program-block"
    );


    element.classList.add(
        getBlockCssClass(
            blockData.type
        )
    );


    element.draggable = true;


    element.dataset.index = index;


    element.textContent =
        getBlockLabel(blockData);


    /* -------------------------
       Drag Start
       ------------------------- */

    element.addEventListener(
        "dragstart",
        (event) => {

            draggedElement = element;
            draggedSource = "program";


            element.classList.add(
                "dragging"
            );


            programDropzone.classList.add(
                "drag-active"
            );


            event.dataTransfer.effectAllowed =
                "move";


            event.dataTransfer.setData(
                "text/plain",
                String(index)
            );

        }
    );


    /* -------------------------
       Drag Ende
       ------------------------- */

    element.addEventListener(
        "dragend",
        () => {

            element.classList.remove(
                "dragging"
            );


            resetDragState();

            clearDropIndicators();

        }
    );


    return element;

}


/* =========================================================
   DROP-INDIKATOR
   ========================================================= */

function createDropIndicator(insertIndex) {

    const indicator =
        document.createElement("div");


    indicator.classList.add(
        "drop-indicator"
    );


    indicator.dataset.insertIndex =
        insertIndex;


    return indicator;

}


/*
 * Zeigt genau an, wo der Block beim Loslassen
 * eingefügt wird.
 */
function activateDropIndicator(index) {

    currentDropIndex = index;


    document
        .querySelectorAll(
            ".drop-indicator"
        )
        .forEach((indicator) => {

            const indicatorIndex =
                Number(
                    indicator.dataset.insertIndex
                );


            indicator.classList.toggle(
                "active",
                indicatorIndex === index
            );

        });

}


/*
 * Alle Einfüge-Markierungen wieder verstecken.
 */
function clearDropIndicators() {

    currentDropIndex = null;


    document
        .querySelectorAll(
            ".drop-indicator.active"
        )
        .forEach((indicator) => {

            indicator.classList.remove(
                "active"
            );

        });


    programDropzone.classList.remove(
        "drag-active"
    );

}


/* =========================================================
   LABEL
   ========================================================= */

function getBlockLabel(block) {

    switch (block.type) {

        case "INT":
            return "int";

        case "STRING":
            return "String";

        case "BOOLEAN":
            return "boolean";

        case "VAR_NAME":
            return block.name;

        case "VALUE":

            if (
                block.value.type === "STRING"
            ) {

                return `"${block.value.value}"`;

            }

            return String(
                block.value.value
            );

        case "EQUALS":
            return "=";

        case "ADD":
            return "+";

        case "SUBTRACT":
            return "-";

        case "MULTIPLY":
            return "*";

        case "DIVIDE":
            return "/";

        case "BREAK":
            return ";";

        default:
            return block.type;

    }

}


/* =========================================================
   CSS-KLASSE
   ========================================================= */

function getBlockCssClass(type) {

    switch (type) {

        case "INT":
        case "STRING":
        case "BOOLEAN":

            return "block-type";


        case "VAR_NAME":

            return "block-variable";


        case "VALUE":

            return "block-value";


        case "BREAK":

            return "block-break";


        default:

            return "block-operator";

    }

}


/* =========================================================
   RESET
   ========================================================= */

resetButton.addEventListener(
    "click",
    () => {

        program = [];

        renderProgram();

        showMessage("");

    }
);


/* =========================================================
   AUSFÜHREN
   ========================================================= */

runButton.addEventListener(
    "click",
    async () => {

        if (program.length === 0) {

            showMessage(
                "Das Programm enthält noch keine Blöcke.",
                true
            );

            return;
        }


        /*
         * Für die Sandbox-Demo erstmal feste Werte.
         *
         * Später kommen userId und levelId
         * aus Benutzerverwaltung bzw. Level.
         *
         * languageId = 1 entspricht aktuell Java.
         */
        const programRequest = {
            userId: 1,
            levelId: 1,
            languageId: 1,
            program: program
        };


        /*
         * Hilfreich für die Demo / Fehlersuche:
         * So sieht man exakt, was ans Backend geht.
         */
        console.log(
            "ProgramRequest an Interpreter:",
            programRequest
        );


        try {

            const response = await fetch(
                "/game/interpreter/run",
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify(programRequest)
                }
            );


            /*
             * Falls das Backend einen Fehler zurückgibt,
             * Text auslesen und anzeigen.
             */
            if (!response.ok) {

                const errorText =
                    await response.text();


                throw new Error(
                    errorText ||
                    `Interpreter-Fehler: HTTP ${response.status}`
                );
            }


            /*
             * Der Interpreter gibt momentan List<String>
             * zurück.
             *
             * Aktuell wird diese Liste vermutlich noch []
             * sein, weil der Service seine Ergebnisse
             * über System.out.println ausgibt.
             */
            const output =
                await response.json();


            console.log(
                "Antwort vom Interpreter:",
                output
            );


            showMessage(
                "Programm wurde erfolgreich vom Interpreter ausgeführt."
            );

        }

        catch (error) {

            console.error(
                "Fehler beim Interpreter-Aufruf:",
                error
            );


            showMessage(
                error.message ||
                "Programm konnte nicht ausgeführt werden.",
                true
            );

        }

    }
);


/* =========================================================
   HELPER
   ========================================================= */

function resetDragState() {

    draggedElement = null;
    draggedSource = null;
    currentDropIndex = null;


    programDropzone.classList.remove(
        "drag-over"
    );

}


function showMessage(
    text,
    isError = false
) {

    message.textContent = text;


    message.classList.toggle(
        "error",
        isError
    );


    message.classList.toggle(
        "success",
        !isError && Boolean(text)
    );

}


/*
 * Initiale Darstellung
 */
renderProgram();