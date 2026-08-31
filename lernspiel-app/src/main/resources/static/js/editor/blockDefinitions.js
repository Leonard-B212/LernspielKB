/**
 * Definiert zentral alle verfügbaren Code-Blöcke mit Typ, Anzeigetext und CSS-Klasse.
 * Die Definitionen werden von den Editor-Modulen für eine einheitliche Darstellung der Blöcke verwendet.
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

// Ermittelt den im Editor angezeigten Text eines Blocks.
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

// Ermittelt die CSS-Klasse für einen Blocktyp.
export function getBlockCssClass(type) {
    return BLOCK_DEFINITIONS[type]?.cssClass ?? "block-operator";
}