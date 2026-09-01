/**
 * Steuert sämtliche Drag-&-Drop-Interaktionen des Code-Editors.
 *
 * Dazu gehören:
 * - neue Blöcke aus der Palette einfügen
 * - vorhandene Blöcke verschieben
 * - Einfügepositionen anzeigen
 * - Blöcke über den Mülleimer löschen
 *
 * Die eigentlichen Programmdaten werden nicht hier gespeichert.
 * Änderungen erfolgen ausschließlich über editorState.
 */

import { createBlockData } from "./blockFactory.js";

// Erstellt den Drag-&-Drop-Controller für einen Editor.
export function createDragDropController({
    palette,
    programDropzone,
    trashDropzone,
    editorState,
    renderProgram,
    showError
}) {
    let draggedElement = null;
    let draggedSource = null;
    let currentDropIndex = null;

    // Registriert alle benötigten Drag-&-Drop-Eventlistener.
    function initialize() {
        initializePalette();
        initializeProgramDropzone();
        initializeTrashDropzone();
    }

    // Macht alle Blöcke aus der Palette als neue Blöcke ziehbar.
    function initializePalette() {
        palette.querySelectorAll(".code-block").forEach((block) => {
            block.addEventListener("dragstart", (event) => {
                draggedElement = block;
                draggedSource = "palette";

                event.dataTransfer.effectAllowed = "copy";
                event.dataTransfer.setData("text/plain", block.dataset.type);
                programDropzone.classList.add("drag-active");
            });

            block.addEventListener("dragend", () => {
                resetDragState();
                clearDropIndicators();
            });
        });
    }

    // Registriert die Drop-Events der eigentlichen Programmfläche.
    function initializeProgramDropzone() {
        programDropzone.addEventListener("dragover", handleProgramDragOver);
        programDropzone.addEventListener("dragleave", handleProgramDragLeave);
        programDropzone.addEventListener("drop", handleProgramDrop);
    }

    // Ermittelt während des Ziehens die aktuell vorgesehene Einfügeposition.
    function handleProgramDragOver(event) {
        event.preventDefault();
        programDropzone.classList.add("drag-over");

        const indicator = event.target.closest(".drop-indicator");

        if (indicator) {
            activateDropIndicator(Number(indicator.dataset.insertIndex));
            return;
        }

        const block = event.target.closest(".program-block");

        if (block) {
            const index = Number(block.dataset.index);
            const rect = block.getBoundingClientRect();
            const mouseIsRightHalf = event.clientX > rect.left + rect.width / 2;

            activateDropIndicator(mouseIsRightHalf ? index + 1 : index);
            return;
        }

        const line = event.target.closest(".code-line");

        if (line) {
            activateDropIndicator(Number(line.dataset.endIndex));
            return;
        }

        activateDropIndicator(editorState.getLength());
    }

    // Entfernt Drop-Markierungen, sobald die Programmfläche verlassen wird.
    function handleProgramDragLeave(event) {
        if (!programDropzone.contains(event.relatedTarget)) {
            programDropzone.classList.remove("drag-over");
            clearDropIndicators();
        }
    }

    // Fügt einen neuen Block ein oder verschiebt einen vorhandenen Programmblock.
    async function handleProgramDrop(event) {
    event.preventDefault();
    programDropzone.classList.remove("drag-over");

    const insertIndex = currentDropIndex ?? editorState.getLength();

    if (draggedSource === "palette") {
        await addPaletteBlock(insertIndex);
    } else if (draggedSource === "program") {
        moveProgramBlock(insertIndex);
    }

    resetDragState();
    clearDropIndicators();
}

    // Erzeugt einen neuen Block aus der Palette und fügt ihn in den State ein.
    async function addPaletteBlock(insertIndex) {
    const type = draggedElement.dataset.type;
    const blockData = await createBlockData(type, showError);

    if (!blockData) {
        return;
    }

    editorState.insertBlock(insertIndex, blockData);
    renderProgram();
}

    // Verschiebt einen bereits vorhandenen Block innerhalb des Programms.
    function moveProgramBlock(insertIndex) {
        const oldIndex = Number(draggedElement.dataset.index);

        editorState.moveBlock(oldIndex, insertIndex);
        renderProgram();
    }

    // Registriert den Mülleimer als Drop-Zone zum Löschen von Programmblöcken.
    function initializeTrashDropzone() {
        trashDropzone.addEventListener("dragover", (event) => {
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

            const index = Number(draggedElement.dataset.index);

            editorState.removeBlock(index);
            renderProgram();
            resetDragState();
        });
    }

    // Initialisiert den Drag-Vorgang eines vorhandenen Programmblocks.
    function handleProgramBlockDragStart(event, element, index) {
        draggedElement = element;
        draggedSource = "program";

        element.classList.add("dragging");
        programDropzone.classList.add("drag-active");

        event.dataTransfer.effectAllowed = "move";
        event.dataTransfer.setData("text/plain", String(index));
    }

    // Räumt nach dem Ziehen eines vorhandenen Programmblocks den Drag-State auf.
    function handleProgramBlockDragEnd(element) {
        element.classList.remove("dragging");
        resetDragState();
        clearDropIndicators();
    }

    // Markiert visuell die Position, an der der Block eingefügt würde.
    function activateDropIndicator(index) {
        currentDropIndex = index;

        programDropzone.querySelectorAll(".drop-indicator").forEach((indicator) => {
            const indicatorIndex = Number(indicator.dataset.insertIndex);
            indicator.classList.toggle("active", indicatorIndex === index);
        });
    }

    // Entfernt sämtliche sichtbaren Einfüge-Markierungen.
    function clearDropIndicators() {
        currentDropIndex = null;

        programDropzone.querySelectorAll(".drop-indicator.active").forEach((indicator) => {
            indicator.classList.remove("active");
        });

        programDropzone.classList.remove("drag-active");
    }

    // Setzt die internen Informationen des aktuellen Drag-Vorgangs zurück.
    function resetDragState() {
        draggedElement = null;
        draggedSource = null;
        currentDropIndex = null;
        programDropzone.classList.remove("drag-over");
    }

    return {
        initialize,
        handleProgramBlockDragStart,
        handleProgramBlockDragEnd
    };
}