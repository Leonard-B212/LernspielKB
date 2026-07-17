package de.lernspiel.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import org.springframework.http.ResponseEntity;

import de.lernspiel.auth.service.UserService;
import de.lernspiel.auth.security.JwtUtils;
import de.lernspiel.auth.entity.User;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/dbinfo")
    public Map<String, Object> dbInfo() {

        return jdbcTemplate.queryForMap(
            "SELECT DATABASE() as db, USER() as user, @@hostname as host, @@port as port, @@version as version"
        );
    }

    @GetMapping("/create-test-user")
    public ResponseEntity<?> createTestUser() {
        try {
            User user = new User();
            user.setPassword("test123");
            user.setType("TEST");
            user.setClassID(null);
            User saved = userService.addUser(user);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(409).body("Konnte Test-User nicht anlegen: " + e.getMessage());
        }
    }

    @GetMapping("/list-users")
    public List<User> listUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/dropUser")
    public ResponseEntity<?> dropUserTable() {
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS `user`");
            return ResponseEntity.ok("Tabelle 'user' gelöscht.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fehler beim Löschen der Tabelle 'user': " + e.getMessage());
        }
    }

    @GetMapping("/login")
    public ResponseEntity<?> debugLogin(@RequestParam int userID, @RequestParam String password) {
        boolean isAuthenticated = userService.authenticateUser(userID, password);
        if (isAuthenticated) {
            String token = jwtUtils.generateToken(String.valueOf(userID));
            return ResponseEntity.ok("Login erfolgreich. Bearer " + token);
        } else {
            return ResponseEntity.status(401).body("Ungültige Anmeldedaten");
        }
    }

    @GetMapping({"", "/"})
    public String index() {
        return """
            <html>
              <head><meta charset="utf-8"><title>Debug</title></head>
              <body>
                <h1>Auth Debug</h1>
                <ul>
                  <li><a href="/debug/dbinfo">/debug/dbinfo</a></li>
                  <li><a href="/debug/create-test-user">/debug/create-test-user</a></li>
                  <li><a href="/debug/list-users">/debug/list-users</a></li>
                  <li><a href="/debug/login?userID=9999&password=test123">/debug/login?userID=9999&password=test123</a></li>
                </ul>
              </body>
            </html>
            """;
    }
}
