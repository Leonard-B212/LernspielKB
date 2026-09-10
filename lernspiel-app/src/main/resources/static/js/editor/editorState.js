/**
 * Verwaltet den aktuellen Zustand des visuellen Programms.
 *
 * Das Programm wird unabhängig vom DOM als flaches Array gespeichert.
 * Dieses Array ist die zentrale Datenquelle des Editors.
 *
 * Renderer und Drag-&-Drop-Logik verändern deshalb nicht direkt
 * irgendwelche HTML-Elemente als Datenhaltung, sondern arbeiten
 * über diesen State.
 *
 * BREAK-Blöcke markieren später beim Rendern das Ende einer Codezeile.
 */

// Erstellt einen neuen unabhängigen Editor-State.
export function createEditorState() {
    let program = [];

    function resolveContainer(path) {
        return path.reduce((container, step) => container[step.index][step.field], program);
    }

    function getProgram(path = []) {
        return resolveContainer(path);
    }

    function getLength(path = []) {
        return resolveContainer(path).length;
    }

    function insertBlock(path, index, block) {
        resolveContainer(path).splice(index, 0, block);
    }

    function moveBlock(fromPath, fromIndex, toPath, toIndex, count = 1) {
        const movedBlocks = resolveContainer(fromPath).splice(fromIndex, count);
        let targetIndex = toIndex;

        if (pathsEqual(fromPath, toPath) && targetIndex > fromIndex) {
            targetIndex -= count;
        }

        resolveContainer(toPath).splice(targetIndex, 0, ...movedBlocks);
    }

    function removeBlock(path, index, count = 1) {
        resolveContainer(path).splice(index, count);
    }

    function clear() {
        program = [];
    }

    function pathsEqual(a, b) {
        return JSON.stringify(a) === JSON.stringify(b);
    }

    return {
        getProgram,
        getLength,
        insertBlock,
        moveBlock,
        removeBlock,
        clear
    };
}