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
    let draggedContainerPath = null;
    let draggedIndex = null;
    let currentDrop = null; // { containerPathKey, index }

    function initialize() {
        initializePalette();
        initializeProgramDropzone();
        initializeTrashDropzone();
    }

    function initializePalette() {
        palette.querySelectorAll(".code-block").forEach((block) => {
            block.addEventListener("dragstart", (event) => {
                draggedElement = block;
                draggedSource = "palette";
                draggedContainerPath = null;
                draggedIndex = null;

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

    function initializeProgramDropzone() {
        programDropzone.addEventListener("dragover", handleProgramDragOver);
        programDropzone.addEventListener("dragleave", handleProgramDragLeave);
        programDropzone.addEventListener("drop", handleProgramDrop);
    }

    // Findet die am tiefsten verschachtelte Dropzone unter dem Mauszeiger und ermittelt
    // darin die Einfügeposition - genau wie zuvor, nur pro Container statt global.
    function handleProgramDragOver(event) {
        event.preventDefault();

        const dropzone = event.target.closest("[data-container-path]");
        if (!dropzone) {
            return;
        }

        programDropzone.classList.add("drag-over");
        const containerPathKey = dropzone.dataset.containerPath;

        const indicator = event.target.closest(".drop-indicator");
        if (indicator && indicator.dataset.containerPath === containerPathKey) {
            activateDropIndicator(containerPathKey, Number(indicator.dataset.insertIndex));
            return;
        }

        const block = event.target.closest(".program-block");
        if (block && block.dataset.containerPath === containerPathKey) {
            const index = Number(block.dataset.index);
            const rect = block.getBoundingClientRect();
            const mouseIsRightHalf = event.clientX > rect.left + rect.width / 2;
            activateDropIndicator(containerPathKey, mouseIsRightHalf ? index + 1 : index);
            return;
        }

        const line = event.target.closest(".code-line");
        if (line && line.dataset.containerPath === containerPathKey) {
            activateDropIndicator(containerPathKey, Number(line.dataset.endIndex));
            return;
        }

        activateDropIndicator(containerPathKey, Number(dropzone.dataset.length ?? 0));
    }

    function handleProgramDragLeave(event) {
        if (!programDropzone.contains(event.relatedTarget)) {
            programDropzone.classList.remove("drag-over");
            clearDropIndicators();
        }
    }

    async function handleProgramDrop(event) {
        event.preventDefault();
        programDropzone.classList.remove("drag-over");

        const targetPath = currentDrop ? JSON.parse(currentDrop.containerPathKey) : [];
        const targetIndex = currentDrop ? currentDrop.index : editorState.getLength(targetPath);

        if (draggedSource === "palette") {
            await addPaletteBlock(targetPath, targetIndex);
        } else if (draggedSource === "program") {
            moveProgramBlock(targetPath, targetIndex);
        }

        resetDragState();
        clearDropIndicators();
    }

    async function addPaletteBlock(targetPath, targetIndex) {
        const type = draggedElement.dataset.type;
        const blockData = await createBlockData(type, showError);

        if (!blockData) {
            return;
        }

        editorState.insertBlock(targetPath, targetIndex, blockData);
        renderProgram();
    }

    let draggedSpanCount = 1;

    function handleProgramBlockDragStart(event, element, path, index, spanCount = 1) {
        draggedElement = element;
        draggedSource = "program";
        draggedContainerPath = path;
        draggedIndex = index;
        draggedSpanCount = spanCount;

        element.classList.add("dragging");
        programDropzone.classList.add("drag-active");

        event.dataTransfer.effectAllowed = "move";
        event.dataTransfer.setData("text/plain", String(index));
    }

    function moveProgramBlock(targetPath, targetIndex) {
        if (isOwnDescendant(targetPath)) return;
        editorState.moveBlock(draggedContainerPath, draggedIndex, targetPath, targetIndex, draggedSpanCount);
        renderProgram();
    }

    function isOwnDescendant(targetPath) {
        if (draggedContainerPath === null || targetPath.length < draggedContainerPath.length + 1) return false;
        for (let i = 0; i < draggedContainerPath.length; i++) {
            if (targetPath[i].index !== draggedContainerPath[i].index || targetPath[i].field !== draggedContainerPath[i].field) return false;
        }
        const childIndex = targetPath[draggedContainerPath.length].index;
        return childIndex >= draggedIndex && childIndex < draggedIndex + draggedSpanCount;
    }

    function initializeTrashDropzone() {
        trashDropzone.addEventListener("dragover", (event) => {
            if (draggedSource !== "program") return;
            event.preventDefault();
            trashDropzone.classList.add("drag-over");
            clearDropIndicators();
        });

        trashDropzone.addEventListener("drop", (event) => {
            event.preventDefault();
            trashDropzone.classList.remove("drag-over");

            if (draggedSource !== "program") {
                resetDragState();
                return;
            }

            editorState.removeBlock(draggedContainerPath, draggedIndex, draggedSpanCount);
            renderProgram();
            resetDragState();
        });
    }

    function handleProgramBlockDragEnd(element) {
        element.classList.remove("dragging");
        resetDragState();
        clearDropIndicators();
    }

    function activateDropIndicator(containerPathKey, index) {
        currentDrop = { containerPathKey, index };

        programDropzone.querySelectorAll(".drop-indicator").forEach((ind) => {
            const matches = ind.dataset.containerPath === containerPathKey && Number(ind.dataset.insertIndex) === index;
            ind.classList.toggle("active", matches);
        });
    }

    function clearDropIndicators() {
        currentDrop = null;
        programDropzone.querySelectorAll(".drop-indicator.active").forEach((ind) => ind.classList.remove("active"));
        programDropzone.classList.remove("drag-active");
    }

    function resetDragState() {
        draggedElement = null;
        draggedSource = null;
        draggedContainerPath = null;
        draggedIndex = null;
        draggedSpanCount = 1;
        programDropzone.classList.remove("drag-over");
    }

    return {
        initialize,
        handleProgramBlockDragStart,
        handleProgramBlockDragEnd
    };
}