/**
 * Steuert die Administratoroberfläche für Benutzer- und Klassenverwaltung.
 *
 * Die Datei bindet die Formulare und Aktionen der Admin-Seite an die Backend-API,
 * lädt Benutzer und Klassen und aktualisiert die zugehörigen Tabellen und Auswahlfelder.
 */

import { apiRequest, logout, requireRole, showMessage } from "../api/api.js";

document.getElementById("logout-button").addEventListener("click", logout);
document.getElementById("reload-classes").addEventListener("click", loadClasses);
document.getElementById("reload-users").addEventListener("click", loadUsers);

document.getElementById("teacher-form").addEventListener("submit", async (event) => {
    event.preventDefault();

    const message = document.getElementById("teacher-message");

    try {
        const password = document.getElementById("teacher-password").value;

        const user = await apiRequest("/api/benutzer/register/teacher", {
            method: "POST",
            body: JSON.stringify({ password })
        });

        showMessage(message, `Lehrer mit ID ${user.userID} erstellt.`);
        event.target.reset();

        await loadUsers();
    } catch (error) {
        showMessage(message, error.message, true);
    }
});

document.getElementById("class-form").addEventListener("submit", async (event) => {
    event.preventDefault();

    const message = document.getElementById("class-message");

    try {
        const className = document.getElementById("class-name").value.trim();
        const teacherID = Number(document.getElementById("class-teacher-id").value);

        const schoolClass = await apiRequest("/api/classes", {
            method: "POST",
            body: JSON.stringify({ className, teacherID })
        });

        showMessage(message, `Klasse ${schoolClass.className} erstellt.`);
        event.target.reset();

        await loadClasses();
    } catch (error) {
        showMessage(message, error.message, true);
    }
});

document.getElementById("student-form").addEventListener("submit", async (event) => {
    event.preventDefault();

    const message = document.getElementById("student-message");

    try {
        const classID = Number(document.getElementById("student-class-id").value);
        const password = document.getElementById("student-password").value;

        const user = await apiRequest("/api/benutzer/register/student", {
            method: "POST",
            body: JSON.stringify({ classID, password })
        });

        showMessage(message, `Schüler mit ID ${user.userID} erstellt.`);
        event.target.reset();

        await loadUsers();
    } catch (error) {
        showMessage(message, error.message, true);
    }
});

document.getElementById("delete-user-form").addEventListener("submit", async (event) => {
    event.preventDefault();

    const message = document.getElementById("delete-user-message");
    const userID = Number(document.getElementById("delete-user-id").value);

    if (!userID) {
        showMessage(message, "Bitte eine gültige User-ID eingeben.", true);
        return;
    }

    const confirmed = window.confirm(
        `Soll der Benutzer mit der ID ${userID} wirklich gelöscht werden?`
    );

    if (!confirmed) {
        return;
    }

    try {
        await apiRequest(`/api/benutzer/${userID}`, {
            method: "DELETE"
        });

        showMessage(message, `Benutzer mit ID ${userID} wurde gelöscht.`);
        event.target.reset();

        await loadUsers();
        await loadClasses();
    } catch (error) {
        showMessage(message, error.message, true);
    }
});

// Lädt alle Klassen und aktualisiert Tabelle sowie Klassenauswahl.
async function loadClasses() {
    const table = document.getElementById("classes-table");
    const select = document.getElementById("student-class-id");
    const message = document.getElementById("classes-message");

    try {
        const classes = await apiRequest("/api/classes");

        table.innerHTML = "";
        select.innerHTML = '<option value="">Klasse wählen</option>';

        classes.forEach((schoolClass) => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${schoolClass.classID}</td>
                <td>${escapeHtml(schoolClass.className)}</td>
                <td>${schoolClass.teacherID}</td>
            `;

            table.appendChild(row);

            const option = document.createElement("option");
            option.value = schoolClass.classID;
            option.textContent = `${schoolClass.className} (ID ${schoolClass.classID})`;

            select.appendChild(option);
        });

        showMessage(message, classes.length ? "" : "Noch keine Klassen vorhanden.");
    } catch (error) {
        showMessage(message, error.message, true);
    }
}

// Lädt alle Benutzer und aktualisiert die Benutzertabelle.
async function loadUsers() {
    const table = document.getElementById("users-table");
    const message = document.getElementById("users-message");

    try {
        const users = await apiRequest("/api/benutzer");

        table.innerHTML = "";

        users.forEach((user) => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${user.userID}</td>
                <td>${escapeHtml(user.type)}</td>
                <td>${user.classID ?? "-"}</td>
                <td>
                    <button
                        type="button"
                        class="danger delete-user-button"
                        data-user-id="${user.userID}">
                        Löschen
                    </button>
                </td>
            `;

            table.appendChild(row);
        });

        document.querySelectorAll(".delete-user-button").forEach((button) => {
            button.addEventListener("click", () => {
                document.getElementById("delete-user-id").value = button.dataset.userId;
                document.getElementById("delete-user-id").focus();
            });
        });

        showMessage(message, users.length ? "" : "Noch keine Benutzer vorhanden.");
    } catch (error) {
        showMessage(message, error.message, true);
    }
}

// Maskiert einen Wert für die sichere Verwendung innerhalb von HTML-Inhalten.
function escapeHtml(value) {
    const div = document.createElement("div");
    div.textContent = value ?? "";
    return div.innerHTML;
}

await requireRole("ADMIN");
await Promise.all([
    loadClasses(),
    loadUsers()
]);