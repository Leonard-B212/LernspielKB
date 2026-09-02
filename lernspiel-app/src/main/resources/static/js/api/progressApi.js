/**
 * API-Zugriff auf den Level-Fortschritt des aktuell
 * authentifizierten Benutzers.
 *
 * Die Requests laufen über apiRequest(), damit der gespeicherte
 * JWT automatisch im Authorization-Header mitgesendet wird.
 */

import { apiRequest } from "./api.js";

// Lädt die IDs aller bereits abgeschlossenen Level des aktuell angemeldeten Benutzers.
export async function getCompletedLevels() {
    return apiRequest("/api/progress/completed-levels");
}

// Markiert ein Level für den aktuell angemeldeten Benutzer als abgeschlossen.
export async function completeLevel(levelID) {
    return apiRequest(`/api/progress/levels/${levelID}/complete`, {
        method: "POST"
    });
}