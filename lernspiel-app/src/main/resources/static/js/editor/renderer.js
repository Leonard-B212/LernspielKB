/**
 * Rendert den aktuellen Editor-State als sichtbare Codezeilen.
 *
 * Der Renderer kennt die Blockdaten, verändert sie aber nicht.
 * Er erhält das aktuelle program-Array und erzeugt daraus das DOM.
 *
 * Drag-&-Drop-Events einzelner Programmblöcke werden über Callbacks
 * an den DragDropController weitergereicht.
 */

import { getBlockCssClass, getBlockLabel } from "./blockDefinitions.js";

// Erstellt einen Renderer für die übergebene Programm-Dropzone.
export function createProgramRenderer({
    programDropzone,
    onBlockDragStart,
    onBlockDragEnd
}) {
    // Zeichnet das komplette Programm anhand des aktuellen States neu.
    function render(program) {
        programDropzone.innerHTML = "";

        const lines = buildProgramLines(program);

        lines.forEach((lineData, lineIndex) => {
            const line = createCodeLine(lineData, lineIndex);
            programDropzone.appendChild(line);
        });
    }

    // Erstellt das DOM-Element für eine einzelne sichtbare Codezeile.
    function createCodeLine(lineData, lineIndex) {
        const line = document.createElement("div");

        line.classList.add("code-line");
        line.dataset.endIndex = lineData.endIndex;

        const lineNumber = document.createElement("div");

        lineNumber.classList.add("line-number");
        lineNumber.textContent = lineIndex + 1;
        line.appendChild(lineNumber);

        const content = document.createElement("div");

        content.classList.add("code-line-content");
        content.appendChild(createDropIndicator(lineData.startIndex));

        lineData.blocks.forEach(({ block, index }) => {
            content.appendChild(createProgramBlock(block, index));
            content.appendChild(createDropIndicator(index + 1));
        });

        if (lineData.blocks.length === 0) {
            const hint = document.createElement("span");

            hint.classList.add("empty-line-hint");
            hint.textContent = lineIndex === 0
                ? "Ziehe Code-Blöcke hier hinein"
                : "Nächste Codezeile";

            content.appendChild(hint);
        }

        line.appendChild(content);

        return line;
    }

    // Erstellt einen einzelnen sichtbaren und verschiebbaren Programmblock.
    function createProgramBlock(blockData, index) {
        const element = document.createElement("div");

        element.classList.add(
            "code-block",
            "program-block",
            getBlockCssClass(blockData.type)
        );

        element.draggable = true;
        element.dataset.index = index;
        element.textContent = getBlockLabel(blockData);

        element.addEventListener("dragstart", (event) => {
            onBlockDragStart(event, element, index);
        });

        element.addEventListener("dragend", () => {
            onBlockDragEnd(element);
        });

        return element;
    }

    // Erstellt eine mögliche Einfügeposition zwischen zwei Code-Blöcken.
    function createDropIndicator(insertIndex) {
        const indicator = document.createElement("div");

        indicator.classList.add("drop-indicator");
        indicator.dataset.insertIndex = insertIndex;

        return indicator;
    }

    return {
        render
    };
}

// Teilt das flache Programm anhand von BREAK-Blöcken in sichtbare Codezeilen auf.
function buildProgramLines(program) {
    const lines = [];

    let currentLine = {
        startIndex: 0,
        blocks: []
    };

    program.forEach((block, index) => {
        currentLine.blocks.push({
            block,
            index
        });

        if (block.type === "BREAK") {
            currentLine.endIndex = index + 1;
            lines.push(currentLine);

            currentLine = {
                startIndex: index + 1,
                blocks: []
            };
        }
    });

    if (currentLine.blocks.length > 0) {
        currentLine.endIndex = program.length;
        lines.push(currentLine);
    }

    if (program.length === 0) {
        lines.push({
            startIndex: 0,
            endIndex: 0,
            blocks: []
        });
    } else if (program[program.length - 1].type === "BREAK") {
        lines.push({
            startIndex: program.length,
            endIndex: program.length,
            blocks: []
        });
    }

    return lines;
}