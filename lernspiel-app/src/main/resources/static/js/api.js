const TOKEN_KEY = "lernspiel_token";

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY);
}

export async function apiRequest(path, options = {}) {
  const headers = new Headers(options.headers || {});
  const token = getToken();

  if (token) {
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

export async function getCurrentUser() {
  return apiRequest("/api/benutzer/me");
}

export function logout() {
  clearToken();
  window.location.href = "/index.html";
}

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

export function redirectByRole(role) {
  const routes = {
    ADMIN: "/admin.html",
    TEACHER: "/teacher.html",
    STUDENT: "/student.html"
  };
  window.location.href = routes[role] || "/index.html";
}

export function showMessage(element, message, isError = false) {
  element.textContent = message;
  element.classList.toggle("error", isError);
  element.classList.toggle("success", !isError && Boolean(message));
}
