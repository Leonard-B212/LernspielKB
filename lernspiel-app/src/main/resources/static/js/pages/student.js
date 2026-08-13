import { logout, requireRole, showMessage } from "../api/api.js";

document.getElementById("logout-button").addEventListener("click", logout);

try {
  const user = await requireRole("STUDENT");
  document.getElementById("profile-user-id").textContent = user.userID;
  document.getElementById("profile-role").textContent = user.type;
  document.getElementById("profile-class-id").textContent = user.classID ?? "Keine Klasse";
} catch (error) {
  showMessage(document.getElementById("profile-message"), error.message, true);
}
