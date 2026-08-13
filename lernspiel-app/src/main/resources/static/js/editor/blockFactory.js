/**
 * Erzeugt die Datenobjekte neuer Code-Blöcke.
 *
 * Einfache Blöcke wie INT oder ADD benötigen nur ihren Typ.
 * Komplexere Blöcke wie VAR_NAME und VALUE benötigen zusätzliche
 * Eingaben des Benutzers.
 *
 * Die erzeugten Objekte entsprechen bereits weitgehend der Struktur,
 * die später als JSON an das Backend bzw. den Interpreter gesendet wird.
 */


/**
 * Erzeugt anhand eines Blocktyps das passende Datenobjekt.
 *
 * onError wird als Callback übergeben, damit dieses Modul selbst
 * keine direkte Abhängigkeit von der Sandbox-Oberfläche besitzt.
 */
export function createBlockData(type, onError = () => {}) {
    switch (type) {
        case "VAR_NAME":
            return createVariableBlock(onError);

        case "VALUE":
            return createValueBlock(onError);

        default:
            return {
                type
            };
    }
}


/**
 * Fragt einen Variablennamen ab, validiert ihn und erzeugt einen VAR_NAME-Block.
 */
function createVariableBlock(onError) {
    const name = window.prompt(
        "Wie soll die Variable heißen?"
    );

    if (name === null) {
        return null;
    }

    const cleanedName = name.trim();

    if (!cleanedName) {
        onError(
            "Der Variablenname darf nicht leer sein."
        );

        return null;
    }

    const validName =
        /^[a-zA-Z_$][a-zA-Z0-9_$]*$/.test(cleanedName);

    if (!validName) {
        onError(
            "Ungültiger Variablenname."
        );

        return null;
    }

    return {
        type: "VAR_NAME",
        name: cleanedName
    };
}


/**
 * Fragt Datentyp und Inhalt eines Wertes ab und erzeugt einen VALUE-Block.
 */
function createValueBlock(onError) {
    const typeInput = window.prompt(
        "Welchen Datentyp hat der Wert?\n\n" +
        "INT\nSTRING\nBOOLEAN",
        "INT"
    );

    if (typeInput === null) {
        return null;
    }

    const valueType =
        typeInput.trim().toUpperCase();

    if (
        !["INT", "STRING", "BOOLEAN"]
            .includes(valueType)
    ) {
        onError(
            "Datentyp muss INT, STRING oder BOOLEAN sein."
        );

        return null;
    }

    const rawValue = window.prompt(
        `Wert für ${valueType}:`
    );

    if (rawValue === null) {
        return null;
    }

    const parsedValue =
        parseValue(rawValue, valueType, onError);

    if (parsedValue === undefined) {
        return null;
    }

    return {
        type: "VALUE",

        value: {
            value: parsedValue,
            type: valueType
        }
    };
}


/**
 * Konvertiert eine Benutzereingabe passend zum gewählten Datentyp.
 */
function parseValue(rawValue, valueType, onError) {
    switch (valueType) {
        case "INT": {
            const value = Number(rawValue);

            if (!Number.isInteger(value)) {
                onError(
                    "Bitte eine gültige ganze Zahl eingeben."
                );

                return undefined;
            }

            return value;
        }

        case "BOOLEAN": {
            const normalized =
                rawValue.toLowerCase();

            if (
                normalized !== "true" &&
                normalized !== "false"
            ) {
                onError(
                    "Boolean muss true oder false sein."
                );

                return undefined;
            }

            return normalized === "true";
        }

        case "STRING":
            return rawValue;

        default:
            return undefined;
    }
}