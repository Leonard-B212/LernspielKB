package de.lernspiel.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import de.lernspiel.auth.entity.*;
import de.lernspiel.auth.repository.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Servicelogik für Controller. Methoden wurden abhängig der Komplexität entsprechend auskommentiert.
 */

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired  
    private UserRepository userRepository;  

    // JULIAN - Alle Benutzerprofile abrufen
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Benutzerprofil nach Username finden
    public Optional<User> getUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
    
        if (optionalUser.isEmpty()) {
            logger.warn("Benutzer mit dem Benutzernamen '{}' existiert nicht.", username);
            return Optional.empty();
        }
        return optionalUser;
    }

    //Benutzerprofil nach Email finden
    public Optional<User> getUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
    
        if (optionalUser.isEmpty()) {
            logger.warn("Benutzer mit der E-Mail '{}' existiert nicht.", email);
            return Optional.empty();
        }
        return optionalUser;
    }

    // Benutzerprofil hinzufügen für Registrierungsmethode
    public User addUser(User user) {
        if(userRepository.existsByUsername(user.getUsername())){
            logger.warn("Benutzername '{}' ist bereits vergeben!", user.getUsername());   
            throw new IllegalArgumentException("Benutzername ist bereits vergeben");
        }if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("Die E-Mail '{}' wird bereits verwendet!", user.getEmail());
            throw new IllegalArgumentException("Die E-Mail-Adresse wird bereits verwendet");
        }

        String[] defaultImages = {          
        "01_bruh.png",
        "02_surprised_pikachu.png",
        "03_what.png",
        "04_1_bitte.png",
        "05_rubbing.png",
        "06_cat.png",
        "07_crazy_women.png",
        "08_today_I_feel_gay.png",
        "09_sad_shibu.png",
        "10_chad_shibu.png",
        "11_winnie.png",
        "12_normalste_harry_henningsen_vorlesung.png",
        "13_giorgi_aliens.png",
        "14_fry.png",
        "15_service.png",
        "16_the_GOAT.png",
        "17_trade_offer.png",
        "18_ok.png",
        "19_oh_nice.png",
        "20_gawk_gawk_3000.png",
        "21_pain_harold.png"
        };

        // zuweisung eines zufälligen Profilfotos aus /Imgs
        String randomProfilePicture = defaultImages[new Random().nextInt(defaultImages.length)];   
        user.setProfilePicture(randomProfilePicture);

        //Passwort hashen und speichern
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    //JULIAN - Profilbild ändern
    
    public User updateProfilePicture(int id, String newProfilePicture) {
        Optional<User> optionalUser = userRepository.findById(id);
    
        if (optionalUser.isEmpty()) {
            logger.warn("Benutzer mit ID '{}' existiert nicht", id);
            throw new IllegalArgumentException("Benutzer mit der ID " +id+ "existiert nicht.");
        }
    
        User user = optionalUser.get();
        user.setProfilePicture("/Imgs/" + newProfilePicture);  
    
        return userRepository.save(user);  
    }

    // JULIAN - Benutzerprofil löschen
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }


    //Benutzerauthentifizierung für Login
    public boolean authenticateUser(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            if (passwordEncoder.matches(password, user.getPassword())) {
                return true; 
            }
        }
        return false; 
    }
}
