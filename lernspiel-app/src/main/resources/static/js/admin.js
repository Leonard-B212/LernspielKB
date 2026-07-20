import { apiRequest, logout, requireRole, showMessage } from "./api.js";

document.getElementById("logout-button").addEventListener("click", logout);
document.getElementById("reload-classes").addEventListener("click", loadClasses);

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
  } catch (error) {
    showMessage(message, error.message, true);
  }
});

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

function escapeHtml(value) {
  const div = document.createElement("div");
  div.textContent = value;
  return div.innerHTML;
}

await requireRole("ADMIN");
await loadClasses();

/*
 * Später aktivieren, sobald GET /api/benutzer existiert:
 *
 * async function loadUsers() {
 *   const users = await apiRequest("/api/benutzer");
 *   // Tabelle befüllen
 * }
 */

/*
 * Später aktivieren, sobald DELETE /api/benutzer/{userID} existiert:
 *
 * async function deleteUser(userID) {
 *   await apiRequest(`/api/benutzer/${userID}`, { method: "DELETE" });
 * }
 */
