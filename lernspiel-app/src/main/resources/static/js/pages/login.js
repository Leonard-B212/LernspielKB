/**
 * Steuert den Login und die anschließende rollenabhängige Weiterleitung.
 *
 * Die Datei sendet die Anmeldedaten an das Backend, speichert den erhaltenen JWT
 * und lädt anschließend den authentifizierten Benutzer für die Weiterleitung.
 */

import {
    apiRequest,
    setToken,
    getCurrentUser,
    redirectByRole,
    showMessage
} from "../api/api.js";

const form = document.getElementById("login-form");
const message = document.getElementById("message");

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    showMessage(message, "Login wird geprüft …");

    const userID = Number(document.getElementById("user-id").value);
    const password = document.getElementById("password").value;

    try {
        const loginResponse = await apiRequest(
            "/api/benutzer/login",
            {
                method: "POST",
                body: JSON.stringify({ userID, password })
            },
            false
        );

        const token = extractToken(loginResponse);
        setToken(token);

        const user = await getCurrentUser();
        redirectByRole(user.type);
    } catch (error) {
        showMessage(message, error.message || "Login fehlgeschlagen", true);
    }
});

// Extrahiert den JWT aus der Login-Antwort des Backends.
function extractToken(response) {
    if (typeof response === "object" && response.token) {
        return response.token;
    }

    if (typeof response === "string") {
        const bearerIndex = response.indexOf("Bearer ");

        if (bearerIndex !== -1) {
            return response.substring(bearerIndex + 7).trim();
        }
    }

    throw new Error("Token konnte nicht aus der Login-Antwort gelesen werden.");
}