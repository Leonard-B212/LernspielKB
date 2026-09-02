package de.lernspiel.auth.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lernspiel.auth.entity.User;
import de.lernspiel.auth.entity.UserType;
import de.lernspiel.auth.security.JwtUtils;
import de.lernspiel.auth.service.UserService;

/**
 * Stellt Hilfsendpunkte für lokale Entwicklung und Diagnose bereit.
 *
 * Der Controller ermöglicht unter anderem Datenbankabfragen, das Anlegen
 * und Anzeigen von Testbenutzern sowie das Zurücksetzen ausgewählter Tabellen.
 *
 * Diese Datei ist notwendig, da wir nur über API-Zugriff auf die Datenbank haben.
 * In der lokalen Entwicklung kann man so schnell und einfach Testbenutzer anlegen
 * und die Datenbank zurücksetzen.
 *
 * Vor einem Deployment in einer produktiven Umgebung sollte dieser Controller
 * entfernt oder deaktiviert werden, um Sicherheitsrisiken zu vermeiden.
 */
@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${db.new-password}")
    private String newDbPassword;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    // Liefert technische Informationen zur aktuell verbundenen Datenbank.
    @GetMapping("/db-info")
    public Map<String, Object> dbInfo() {
        return jdbcTemplate.queryForMap(
                "SELECT DATABASE() as db, USER() as user, CURRENT_USER() as currentUser, @@hostname as host, @@port as port, @@version as version"
        );
    }

    // Erstellt einen einfachen Testbenutzer für die lokale Entwicklung.
    @GetMapping("/create-test-user")
    public ResponseEntity<?> createTestUser() {
        try {
            User user = new User();
            user.setPassword("test123");
            user.setType(UserType.STUDENT);
            user.setClassID(null);

            User saved = userService.addUser(user);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(409).body(
                    "Konnte Test-User nicht anlegen: " + e.getMessage()
            );
        }
    }

    // Liefert alle aktuell gespeicherten Benutzer.
    @GetMapping("/list-users")
    public List<User> listUsers() {
        return userService.getAllUsers();
    }

    // Löscht Authentifizierungs- und Klassentabellen für einen lokalen Datenbank-Reset.
    @DeleteMapping("/drop-user")
    public ResponseEntity<?> dropUserTable() {
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS school_class");
            jdbcTemplate.execute("DROP TABLE IF EXISTS `user`");

            return ResponseEntity.ok("Tabellen gelöscht.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    "Fehler beim Löschen der Tabelle 'user': " + e.getMessage()
            );
        }
    }

    // Löscht die Level-Tabellen, damit Hibernate sie anhand der Entities neu erzeugen kann.
    @DeleteMapping("/drop-level-data")
    public ResponseEntity<?> dropLevelData() {
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS completed_level");
            jdbcTemplate.execute("DROP TABLE IF EXISTS level_component");
            jdbcTemplate.execute("DROP TABLE IF EXISTS level");
            jdbcTemplate.execute("DROP TABLE IF EXISTS level_category");

            return ResponseEntity.ok("Level-Tabellen wurden gelöscht.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    "Fehler beim Löschen der Level-Tabellen: " + e.getMessage()
            );
        }
    }

    // Liefert die gespeicherten Level inklusive Expected ExecutionLog zur Kontrolle.
    @GetMapping("/levels")
    public List<Map<String, Object>> levels() {
        return jdbcTemplate.queryForList(
                "SELECT levelid, level_name, level_number, expected_execution_log FROM level ORDER BY levelid"
        );
    }

    // Ändert das Passwort des aktuell verwendeten Datenbankbenutzers.
    @PostMapping("/change-db-password")
    public ResponseEntity<?> changeDbPassword() {
        try {
            jdbcTemplate.execute(
                    "SET PASSWORD FOR 'prg_spiel'@'%' = PASSWORD('" +
                    newDbPassword.replace("'", "''") +
                    "')"
            );

            return ResponseEntity.ok("Datenbankpasswort wurde geändert.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    "Datenbankpasswort konnte nicht geändert werden: " + e.getMessage()
            );
        }
    }

    // Liefert eine einfache Übersicht der verfügbaren Debug-Endpunkte.
    @GetMapping({"", "/"})
    public String index() {
        return """
                <html>
                  <head><meta charset="utf-8"><title>Debug</title></head>
                  <body>
                    <h1>Auth Debug</h1>
                    <ul>
                      <li><a href="/debug/db-info">/debug/db-info</a></li>
                      <li><a href="/debug/create-test-user">/debug/create-test-user</a></li>
                      <li><a href="/debug/list-users">/debug/list-users</a></li>
                      <li><a href="/debug/login?userID=9999&password=test123">/debug/login?userID=9999&password=test123</a></li>
                    </ul>
                  </body>
                </html>
                """;
    }
}