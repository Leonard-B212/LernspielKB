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


/**
 * Erstellt einen neuen unabhängigen Editor-State.
 */
export function createEditorState() {
    let program = [];


    /**
     * Liefert das aktuelle Programm als Blockliste.
     */
    function getProgram() {
        return program;
    }


    /**
     * Liefert die aktuelle Anzahl der Blöcke.
     */
    function getLength() {
        return program.length;
    }


    /**
     * Fügt einen neuen Block an einer bestimmten Position ein.
     */
    function insertBlock(index, block) {
        program.splice(index, 0, block);
    }


    /**
     * Verschiebt einen bestehenden Block innerhalb des Programms.
     */
    function moveBlock(oldIndex, newIndex) {
        const movedBlock =
            program.splice(oldIndex, 1)[0];

        if (newIndex > oldIndex) {
            newIndex--;
        }

        program.splice(
            newIndex,
            0,
            movedBlock
        );
    }


    /**
     * Entfernt einen Block anhand seines Index.
     */
    function removeBlock(index) {
        program.splice(index, 1);
    }


    /**
     * Löscht das komplette aktuelle Programm.
     */
    function clear() {
        program = [];
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