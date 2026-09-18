/**
 * Initialisiert die gemeinsame Navigation.
 *
 * Bindet den Logout-Button und ergänzt auf gemeinsamen Seiten
 * rollenabhängige Navigationselemente.
 */

import { getCurrentUser, logout } from "../api/api.js";

// Initialisiert Logout und rollenabhängige Navigation.
export async function initializeNavigation() {
    const logoutButton = document.getElementById("logout-button");

    if (logoutButton) {
        logoutButton.addEventListener("click", logout);
    }

    await initializeDashboardNavigation();
}

// Zeigt Admins und Lehrern auf gemeinsamen Seiten den Rückweg zu ihrem Dashboard.
async function initializeDashboardNavigation() {
    const dashboardNavigation = document.getElementById("dashboard-navigation");

    if (!dashboardNavigation) {
        return;
    }

    try {
        const user = await getCurrentUser();

        const dashboardRoutes = {
            ADMIN: "/admin.html",
            TEACHER: "/teacher.html"
        };

        const dashboardRoute = dashboardRoutes[user.type];

        if (!dashboardRoute) {
            return;
        }

        const link = document.createElement("a");

        link.href = dashboardRoute;
        link.classList.add("topbar-nav-link");
        link.textContent = "← Dashboard";

        dashboardNavigation.appendChild(link);
    } catch (error) {
        console.error("Dashboard-Navigation konnte nicht initialisiert werden:", error);
    }
}

initializeNavigation();