/**
 * Steuert die Lehreroberfläche für Klassen- und Schülerverwaltung.
 *
 * Die Datei prüft die TEACHER-Rolle, lädt die zugehörigen Klassen und Schüler
 * und bindet die Formulare zum Anlegen neuer Klassen und Schüler an die Backend-API.
 */

import { apiRequest, logout, requireRole, showMessage } from "../api/api.js";

const currentTeacher = await requireRole("TEACHER");

document.getElementById("logout-button").addEventListener("click", logout);
document.getElementById("reload-classes").addEventListener("click", loadClasses);
document.getElementById("reload-students").addEventListener("click", loadStudents);

document.getElementById("class-form").addEventListener("submit", async (event) => {
    event.preventDefault();

    const message = document.getElementById("class-message");

    try {
        const className = document.getElementById("class-name").value.trim();

        const schoolClass = await apiRequest("/api/classes", {
            method: "POST",
            body: JSON.stringify({
                className,
                teacherID: currentTeacher.userID
            })
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
            body: JSON.stringify({
                classID,
                password
            })
        });

        showMessage(message, `Schüler mit ID ${user.userID} erstellt.`);
        event.target.reset();

        await loadStudents();
    } catch (error) {
        showMessage(message, error.message, true);
    }
});

// Lädt die dem aktuellen Lehrer zugeordneten Klassen.
async function loadClasses() {
    const table = document.getElementById("classes-table");
    const select = document.getElementById("student-class-id");
    const message = document.getElementById("classes-message");

    try {
        const classes = await apiRequest("/api/classes");

        const ownClasses = classes.filter(
            (schoolClass) => schoolClass.teacherID === currentTeacher.userID
        );

        table.innerHTML = "";
        select.innerHTML = '<option value="">Klasse wählen</option>';

        ownClasses.forEach((schoolClass) => {
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

        showMessage(message, ownClasses.length ? "" : "Du hast noch keine Klassen.");
    } catch (error) {
        table.innerHTML = "";
        showMessage(message, error.message, true);
    }
}

// Lädt die dem aktuellen Lehrer zugeordneten Schüler.
async function loadStudents() {
    const table = document.getElementById("students-table");
    const message = document.getElementById("students-message");

    try {
        const students = await apiRequest("/api/benutzer/me/students");

        table.innerHTML = "";

        students.forEach((student) => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${student.userID}</td>
                <td>${escapeHtml(student.type)}</td>
                <td>${student.classID ?? "Keine Klasse"}</td>
            `;

            table.appendChild(row);
        });

        showMessage(
            message,
            students.length ? "" : "Dir sind aktuell keine Schüler zugewiesen."
        );
    } catch (error) {
        table.innerHTML = "";
        showMessage(message, error.message, true);
    }
}

// Maskiert einen Wert für die sichere Verwendung innerhalb von HTML-Inhalten.
function escapeHtml(value) {
    const div = document.createElement("div");
    div.textContent = value ?? "";
    return div.innerHTML;
}

await Promise.all([
    loadClasses(),
    loadStudents()
]);