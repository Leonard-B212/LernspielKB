/**
 * API-Zugriff auf den Level-Service.
 *
 * Alle Requests laufen über die zentrale apiRequest()-Funktion,
 * damit Fehlerbehandlung und Authentifizierung einheitlich bleiben.
 */

import {
    apiRequest
} from "./api.js";


/**
 * Lädt ein einzelnes Level anhand seiner ID.
 */
export async function getLevel(levelID) {

    return apiRequest(
        `/api/levels/${levelID}`
    );
}


/**
 * Lädt alle verfügbaren Level in kompakter Form.
 */
export async function getAllLevels() {

    return apiRequest(
        "/api/levels"
    );
}