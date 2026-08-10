import {
  apiRequest,
  setToken,
  getCurrentUser,
  redirectByRole,
  showMessage
} from "./api.js";

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

      // GEÄNDERT:
      // Login ist ein öffentlicher Request.
      // Deshalb wird hier bewusst KEIN vorhandener JWT mitgeschickt.
      false
    );

    const token = extractToken(loginResponse);
    setToken(token);

    // Ab hier wird wieder normal authentifiziert:
    // getCurrentUser() verwendet apiRequest() mit useAuth = true.
    const user = await getCurrentUser();

    redirectByRole(user.type);
  } catch (error) {
    showMessage(
      message,
      error.message || "Login fehlgeschlagen",
      true
    );
  }
});

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

  throw new Error(
    "Token konnte nicht aus der Login-Antwort gelesen werden."
  );
}