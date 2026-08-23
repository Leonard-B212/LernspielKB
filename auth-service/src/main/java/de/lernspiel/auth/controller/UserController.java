package de.lernspiel.auth.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lernspiel.auth.dto.LoginRequest;
import de.lernspiel.auth.dto.StudentRegisterRequest;
import de.lernspiel.auth.dto.TeacherRegisterRequest;
import de.lernspiel.auth.dto.UserResponse;
import de.lernspiel.auth.entity.User;
import de.lernspiel.auth.entity.UserType;
import de.lernspiel.auth.security.JwtUtils;
import de.lernspiel.auth.service.UserService;
import jakarta.validation.Valid;

/**
 * REST-Controller für Authentifizierung und Benutzerverwaltung.
 *
 * Stellt Endpunkte für Login, Benutzerprofil, Registrierung,
 * Benutzerlöschung und rollenabhängige Benutzerabfragen bereit.
 */
@RestController
@RequestMapping("/api/benutzer")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    // Wandelt eine User-Entity in das entsprechende Response-DTO um.
    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getUserID(),
                user.getType(),
                user.getClassID()
        );
    }

    // Gibt das Profil des aktuell authentifizierten Benutzers zurück.
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Nicht authentifiziert");
        }

        int userID = Integer.parseInt(authentication.getName());
        Optional<User> optionalUser = userService.getUserById(userID);

        if (optionalUser.isPresent()) {
            return ResponseEntity.ok().body(mapToUserResponse(optionalUser.get()));
        } else {
            return ResponseEntity.status(404).body("Benutzer nicht gefunden");
        }
    }

    // Authentifiziert einen Benutzer und gibt bei Erfolg ein JWT zurück.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        boolean isAuthenticated = userService.authenticateUser(
                loginRequest.getUserID(),
                loginRequest.getPassword()
        );

        if (!isAuthenticated) {
            return ResponseEntity.status(401).body("Ungültige Anmeldedaten");
        }

        Optional<User> optionalUser = userService.getUserById(loginRequest.getUserID());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("Benutzer nicht gefunden");
        }

        User user = optionalUser.get();
        String token = jwtUtils.generateToken(user);

        return ResponseEntity.ok().body("Login erfolgreich. Bearer " + token);
    }

    // Erstellt einen neuen Lehrer. Nur Administratoren dürfen Lehrer erstellen.
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/teacher")
    public ResponseEntity<?> registerTeacher(@RequestBody @Valid TeacherRegisterRequest req) {
        try {
            User teacher = new User();
            teacher.setType(UserType.TEACHER);
            teacher.setClassID(null);
            teacher.setPassword(req.getPassword());

            User savedTeacher = userService.addUser(teacher);

            return ResponseEntity.ok(mapToUserResponse(savedTeacher));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Erstellt einen neuen Schüler. Administratoren und Lehrer dürfen Schüler erstellen.
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping("/register/student")
    public ResponseEntity<?> registerStudent(@RequestBody @Valid StudentRegisterRequest req) {
        try {
            User student = new User();
            student.setType(UserType.STUDENT);
            student.setClassID(req.getClassID());
            student.setPassword(req.getPassword());

            User savedStudent = userService.addStudent(student);

            return ResponseEntity.ok(mapToUserResponse(savedStudent));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Löscht einen Benutzer anhand seiner ID. Nur Administratoren dürfen Benutzer löschen.
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userID}")
    public ResponseEntity<?> deleteUser(@PathVariable int userID) {
        try {
            userService.deleteUser(userID);
            return ResponseEntity.ok("Benutzer erfolgreich gelöscht.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Gibt alle Benutzerprofile zurück. Nur Administratoren dürfen dies tun.
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Gibt alle Schüler des aktuell authentifizierten Lehrers zurück.
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/me/students")
    public ResponseEntity<?> getMyStudents(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Nicht authentifiziert");
        }

        try {
            int teacherID = Integer.parseInt(authentication.getName());

            List<UserResponse> students = userService
                    .getStudentsForTeacher(teacherID)
                    .stream()
                    .map(this::mapToUserResponse)
                    .toList();

            return ResponseEntity.ok(students);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(401).body("Ungültige Benutzeridentität im Token.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}