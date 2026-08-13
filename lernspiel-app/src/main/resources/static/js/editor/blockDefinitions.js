/**
 * Zentrale Definition aller verfügbaren Code-Blöcke.
 *
 * Diese Datei beschreibt, wie ein Block heißt und welche CSS-Klasse
 * für seine Darstellung verwendet wird.
 *
 * Andere Editor-Module greifen auf diese Definitionen zurück,
 * damit Labels und Styles nicht an mehreren Stellen dupliziert werden.
 *
 * Später können Level anhand dieser Typen festlegen,
 * welche Blöcke für eine Aufgabe verfügbar sind.
 */

export const BLOCK_DEFINITIONS = {
    INT: {
        type: "INT",
        label: "int",
        cssClass: "block-type"
    },

    STRING: {
        type: "STRING",
        label: "String",
        cssClass: "block-type"
    },

    BOOLEAN: {
        type: "BOOLEAN",
        label: "boolean",
        cssClass: "block-type"
    },

    VAR_NAME: {
        type: "VAR_NAME",
        label: "Variable",
        cssClass: "block-variable"
    },

    VALUE: {
        type: "VALUE",
        label: "Wert",
        cssClass: "block-value"
    },

    EQUALS: {
        type: "EQUALS",
        label: "=",
        cssClass: "block-operator"
    },

    ADD: {
        type: "ADD",
        label: "+",
        cssClass: "block-operator"
    },

    SUBTRACT: {
        type: "SUBTRACT",
        label: "-",
        cssClass: "block-operator"
    },

    MULTIPLY: {
        type: "MULTIPLY",
        label: "*",
        cssClass: "block-operator"
    },

    DIVIDE: {
        type: "DIVIDE",
        label: "/",
        cssClass: "block-operator"
    },

    BREAK: {
        type: "BREAK",
        label: ";",
        cssClass: "block-break"
    }
};


/**
 * Liefert den Text, der für einen konkreten Block im Editor angezeigt wird.
 * Dynamische Blöcke wie Variablen und Werte verwenden ihre eigenen Daten.
 */
export function getBlockLabel(block) {
    switch (block.type) {
        case "VAR_NAME":
            return block.name;

        case "VALUE":
            if (block.value.type === "STRING") {
                return `"${block.value.value}"`;
            }

            return String(block.value.value);

        default:
            return BLOCK_DEFINITIONS[block.type]?.label ?? block.type;
    }
}


/**
 * Liefert die passende CSS-Klasse für einen bestimmten Blocktyp.
 */
export function getBlockCssClass(type) {
    return BLOCK_DEFINITIONS[type]?.cssClass ?? "block-operator";
}