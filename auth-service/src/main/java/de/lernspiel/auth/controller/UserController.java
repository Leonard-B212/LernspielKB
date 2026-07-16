package de.lernspiel.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.lernspiel.auth.service.*;
import de.lernspiel.auth.dto.LoginRequest;
import de.lernspiel.auth.dto.UserResponse;
import de.lernspiel.auth.entity.*;
import de.lernspiel.auth.security.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

@RestController       
@RequestMapping("/api/benutzer")  
public class UserController {

    @Autowired                          
    private UserService userService;

   @Autowired
   private JwtUtils jwtUtils;

    //JULIAN eig DTO verwenden, aber da testmethode irrelevant, kann eig gelöscht werden, da es keinen echten Mehrwert bietet, da die User-Entity eh nicht direkt an den Client gesendet wird, sondern immer über das UserResponse DTO läuft.
    @GetMapping 	                                
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
            user.getUserID(),
            user.getType(),
            user.getClassID()
        );
    }


    /*
     *  Bereitstellung des Benutzerprofils:
     *  Gibt das aktuelle Benutzerprofil basierend auf der Authentifizierung zurück.
     *  Holt die E-Mail aus dem Authentication-Objekt, also indirekt aus dem Benutzertoken und sucht den Benutzer in der Datenbank.
     *  Gibt bei Erfolg das Profil zurück, ansonsten einen entsprechenden Fehlerstatus.
        */ 
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
            if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(403).body("Nicht authentifiziert");
            }

            int userID = Integer.parseInt(authentication.getName()); // userID aus dem authentication objekt aus dem SecurityContext
            Optional<User> optionalUser = userService.getUserById(userID);

            if (optionalUser.isPresent()) {
                return ResponseEntity.ok().body(mapToUserResponse(optionalUser.get()));
            } else {
                return ResponseEntity.status(404).body("Benutzer nicht gefunden");
        }
    }


    // JULIAN - Profilbild aktualisieren
    @PatchMapping("/{id}/profilbild")    
    public User updateProfilePicture(@PathVariable int id, @RequestBody String newProfilePicture) {
        return userService.updateProfilePicture(id, newProfilePicture);
    }

    //JULIAN - Für Prototyp nicht spezifisch notwendig, allerdings zum verwalten und testen sinnvoll gewesen.
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }

    /*
     * Einloggen:
     * Führt die Benutzer-Authentifizierung durch und gibt bei Erfolg ein JWT-Benutzertoken zurück.
     * Bei gültigen Anmeldedaten wird das Token mit HTTP-Status 200 (OK) zurückgegeben, falls nicht mit 401 unauthorized
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {               
        boolean isAuthenticated = userService.authenticateUser(loginRequest.getUserID(), loginRequest.getPassword());
        if (isAuthenticated) {              
            String token = jwtUtils.generateToken(String.valueOf(user.getUserID()));
            return ResponseEntity.ok().body("Login erfolgreich. Bearer " + token);    
        } else {
            return ResponseEntity.status(401).body("Ungültige Anmeldedaten");  
        }
    }
    

    /*
     * Registriert einen neuen Benutzer, nachdem die Eingaben validiert wurden.
     * Basic level Fehler sollen hierbei sofort zurückgegeben werden, deswegen sind die 3 Tests hier und nicht in der addUser Methode, damit man nicht die Schicht durchlaufen muss
     * Wenn die eingaben passen, wird der Benutzer hinzugefügt. Die addUser methode prüft dann, ob der Nutzer existiert/Name/Email verwendet wurde. 
     * Es werden entsprechende Statusmeldungen ausgegeben
     * 
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) { //regex hab ich mir generieren lassen
        try {
            User savedUser = userService.addUser(user);
            return ResponseEntity.ok().body("Registrierung erfolgreich: " + savedUser.getUserID());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}

