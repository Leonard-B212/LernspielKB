/**
 * Enthält die HTTP-Kommunikation mit dem Level-Service.
 *
 * Die Level-Seite muss dadurch nicht wissen, wie der konkrete
 * REST-Aufruf aufgebaut ist.
 */


/**
 * Lädt ein einzelnes Level anhand seiner ID.
 *
 * @param {number} levelID
 * @returns {Promise<Object>}
 */
export async function getLevel(levelID) {

    const response =
        await fetch(
            `/api/levels/${levelID}`
        );


    if (!response.ok) {

        const errorText =
            await response.text();

        throw new Error(
            errorText ||
            `Level konnte nicht geladen werden: HTTP ${response.status}`
        );
    }


    return response.json();
}