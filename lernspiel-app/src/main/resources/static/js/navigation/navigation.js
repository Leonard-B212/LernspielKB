/**
 * Initialisiert die gemeinsame Navigation.
 *
 * Aktuell wird hier der Logout-Button angebunden.
 * Weitere gemeinsame Navigationselemente können später
 * an dieser Stelle ergänzt werden.
 */

import { logout } from "../api/api.js";

// Initialisiert die gemeinsame Navigation und bindet den Logout-Button an.
export function initializeNavigation() {
    const logoutButton = document.getElementById("logout-button");

    if (logoutButton) {
        logoutButton.addEventListener("click", logout);
    }
}

initializeNavigation();