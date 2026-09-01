/**
 * Erzeugt und validiert die Datenobjekte neuer Code-Blöcke.
 * Konfigurierbare Eingaben werden über blockInput.js abgefragt.
 */

import { requestVariableName, requestValue } from "./blockInput.js";

export async function createBlockData(type, onError = () => {}) {
    switch (type) {
        case "VAR_NAME":
            return createVariableBlock(onError);

        case "VALUE":
            return createValueBlock(onError);

        default:
            return { type };
    }
}

async function createVariableBlock(onError) {
    const name = await requestVariableName();

    if (name === null) {
        return null;
    }

    const cleanedName = name.trim();

    if (!cleanedName) {
        onError("Der Variablenname darf nicht leer sein.");
        return null;
    }

    if (!/^[a-zA-Z_$][a-zA-Z0-9_$]*$/.test(cleanedName)) {
        onError("Ungültiger Variablenname.");
        return null;
    }

    return {
        type: "VAR_NAME",
        name: cleanedName
    };
}

async function createValueBlock(onError) {
    const input = await requestValue();

    if (!input) {
        return null;
    }

    const parsedValue = parseValue(input.value, input.type, onError);

    if (parsedValue === undefined) {
        return null;
    }

    return {
        type: "VALUE",
        value: {
            value: parsedValue,
            type: input.type
        }
    };
}

// Konvertiert die Eingabe passend zum ausgewählten Datentyp.
function parseValue(rawValue, valueType, onError) {
    switch (valueType) {
        case "INT": {
            const value = Number(rawValue);

            if (!Number.isInteger(value)) {
                onError("Bitte eine gültige ganze Zahl eingeben.");
                return undefined;
            }

            return value;
        }

        case "BOOLEAN":
            return Boolean(rawValue);

        case "STRING":
            return rawValue;

        default:
            return undefined;
    }
}