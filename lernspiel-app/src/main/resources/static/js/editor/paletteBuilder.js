import {
    BLOCK_DEFINITIONS
} from "./blockDefinitions.js";


/**
 * Erstellt die Block-Palette für ein Level.
 *
 * Zu den vom Backend vorgegebenen Level-Komponenten werden
 * zufällig 2 bis 3 zusätzliche Blocktypen hinzugefügt.
 *
 * Anschließend wird die gesamte Palette zufällig gemischt,
 * damit weder die vorhandenen Blöcke noch ihre Reihenfolge
 * die Lösung einer Aufgabe direkt vorgeben.
 */
export function buildLevelPalette(
    levelComponents) {

    if (
        !Array.isArray(levelComponents)
        || levelComponents.length === 0
    ) {
        return [];
    }


    const components =
        levelComponents.map(
            component => ({
                ...component
            })
        );


    const distractors =
        createDistractors(
            components
        );


    return shuffle([
        ...components,
        ...distractors
    ]);
}


/**
 * Erzeugt 2 bis 3 zufällige zusätzliche Blocktypen,
 * die noch nicht Bestandteil des Levels sind.
 */
function createDistractors(
    levelComponents) {

    const existingTypes =
        new Set(
            levelComponents.map(
                component =>
                    component.type
            )
        );


    /*
     * BLOCK_DEFINITIONS enthält die Blocktypen,
     * die das Frontend aktuell darstellen kann.
     *
     * Bereits im Level vorhandene Typen werden ausgeschlossen.
     */
    const availableTypes =
        Object.keys(
            BLOCK_DEFINITIONS
        ).filter(
            type =>
                !existingTypes.has(
                    type
                )
        );


    /*
     * Auch die möglichen Distraktoren werden zunächst gemischt.
     */
    const shuffledTypes =
        shuffle(
            availableTypes
        );


    /*
     * Zufällig zwei oder drei Distraktoren.
     *
     * Falls weniger Typen verfügbar sind,
     * werden entsprechend weniger verwendet.
     */
    const requestedAmount =
        randomInteger(
            2,
            3
        );

    const distractorAmount =
        Math.min(
            requestedAmount,
            shuffledTypes.length
        );


    return shuffledTypes
        .slice(
            0,
            distractorAmount
        )
        .map(
            type => ({
                type,
                amount: 1
            })
        );
}


/**
 * Mischt eine Liste mit dem Fisher-Yates-Algorithmus.
 *
 * Die ursprüngliche Liste wird dabei nicht verändert.
 */
function shuffle(items) {

    const shuffled = [
        ...items
    ];


    for (
        let index = shuffled.length - 1;
        index > 0;
        index--
    ) {

        const randomIndex =
            Math.floor(
                Math.random()
                * (index + 1)
            );


        [
            shuffled[index],
            shuffled[randomIndex]
        ] = [
            shuffled[randomIndex],
            shuffled[index]
        ];
    }


    return shuffled;
}


/**
 * Liefert eine zufällige ganze Zahl
 * innerhalb des angegebenen Bereichs.
 */
function randomInteger(
    min,
    max) {

    return Math.floor(
        Math.random()
        * (max - min + 1)
    ) + min;
}