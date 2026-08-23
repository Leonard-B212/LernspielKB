/**
 * Kapselt die gemeinsame Frontend-Kommunikation mit dem Backend.
 *
 * Die Datei verwaltet den JWT im Local Storage, führt authentifizierte
 * API-Requests aus und stellt zentrale Hilfsfunktionen für Rollenprüfung,
 * Weiterleitung, Logout und Statusmeldungen bereit.
 */

const TOKEN_KEY = "lernspiel_token";

// Liest den aktuell gespeicherten JWT aus dem Local Storage.
export function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

// Speichert einen JWT im Local Storage.
export function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

// Entfernt den gespeicherten JWT aus dem Local Storage.
export function clearToken() {
    localStorage.removeItem(TOKEN_KEY);
}

// Führt einen API-Request aus und ergänzt bei Bedarf automatisch den JWT.
export async function apiRequest(path, options = {}, useAuth = true) {
    const headers = new Headers(options.headers || {});
    const token = getToken();

    if (useAuth && token) {
        headers.set("Authorization", `Bearer ${token}`);
    }

    if (options.body && !headers.has("Content-Type")) {
        headers.set("Content-Type", "application/json");
    }

    const response = await fetch(path, { ...options, headers });
    const contentType = response.headers.get("content-type") || "";

    const data = contentType.includes("application/json")
        ? await response.json()
        : await response.text();

    if (!response.ok) {
        const message = typeof data === "string"
            ? data
            : data?.message || `HTTP ${response.status}`;

        throw new Error(message || `HTTP ${response.status}`);
    }

    return data;
}

// Lädt den aktuell angemeldeten Benutzer.
export async function getCurrentUser() {
    return apiRequest("/api/benutzer/me");
}

// Entfernt die lokale Anmeldung und leitet zur Login-Seite weiter.
export function logout() {
    clearToken();
    window.location.href = "/index.html";
}

// Prüft die Rolle des angemeldeten Benutzers und leitet bei Abweichungen weiter.
export async function requireRole(...allowedRoles) {
    if (!getToken()) {
        window.location.href = "/index.html";
        throw new Error("Kein Token vorhanden");
    }

    try {
        const user = await getCurrentUser();

        if (!allowedRoles.includes(user.type)) {
            redirectByRole(user.type);
            throw new Error("Falsche Rolle");
        }

        return user;
    } catch (error) {
        if (error.message !== "Falsche Rolle") {
            clearToken();
            window.location.href = "/index.html";
        }

        throw error;
    }
}

// Leitet einen Benutzer anhand seiner Rolle auf die zugehörige Startseite weiter.
export function redirectByRole(role) {
    const routes = {
        ADMIN: "/admin.html",
        TEACHER: "/teacher.html",
        STUDENT: "/skilltree.html"
    };

    window.location.href = routes[role] || "/index.html";
}

// Zeigt eine Statusmeldung an und setzt die passende Erfolgs- oder Fehlerklasse.
export function showMessage(element, message, isError = false) {
    element.textContent = message;
    element.classList.toggle("error", isError);
    element.classList.toggle("success", !isError && Boolean(message));
}