package de.lernspiel.auth.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import de.lernspiel.auth.entity.SchoolClass;
import de.lernspiel.auth.entity.User;
import de.lernspiel.auth.entity.UserType;
import de.lernspiel.auth.repository.SchoolClassRepository;
import de.lernspiel.auth.repository.UserRepository;

/**
 * Verwaltet die Servicelogik für Benutzer, Authentifizierung und Schülerzuordnungen.
 *
 * Der Service übernimmt unter anderem Benutzerzugriffe, Passwort-Hashing,
 * Login-Prüfung sowie die Zuordnung von Schülern zu Klassen und Lehrern.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    // Liefert alle vorhandenen Benutzer.
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Liefert einen Benutzer anhand seiner ID.
    public Optional<User> getUserById(int userID) {
        Optional<User> optionalUser = userRepository.findById(userID);

        if (optionalUser.isEmpty()) {
            logger.warn("Benutzer mit der ID '{}' existiert nicht.", userID);
            return Optional.empty();
        }

        return optionalUser;
    }

    // Hasht das Passwort und speichert einen neuen Benutzer.
    public User addUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    // Legt einen Schüler an, sofern die angegebene Klasse existiert.
    public User addStudent(User student) {
        if (student.getClassID() == null) {
            throw new IllegalArgumentException("Für einen Schüler muss eine Klasse angegeben werden.");
        }

        if (!schoolClassRepository.existsById(student.getClassID())) {
            throw new IllegalArgumentException("Die angegebene Klasse existiert nicht.");
        }

        return addUser(student);
    }

    // Löscht einen Benutzer anhand seiner ID.
    public void deleteUser(int userID) {
        if (!userRepository.existsById(userID)) {
            throw new IllegalArgumentException("Benutzer mit der ID " + userID + " existiert nicht.");
        }

        userRepository.deleteById(userID);
    }

    // Prüft die Anmeldedaten eines Benutzers.
    public boolean authenticateUser(int userID, String password) {
        Optional<User> optionalUser = userRepository.findById(userID);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (passwordEncoder.matches(password, user.getPassword())) {
                return true;
            }
        }

        return false;
    }

    // Liefert alle Schüler aus den Klassen eines bestimmten Lehrers.
    public List<User> getStudentsForTeacher(int teacherID) {
        User teacher = userRepository.findById(teacherID)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lehrer mit der ID " + teacherID + " wurde nicht gefunden."
                ));

        if (teacher.getType() != UserType.TEACHER) {
            throw new IllegalArgumentException("Der angemeldete Benutzer ist kein Lehrer.");
        }

        List<SchoolClass> teacherClasses = schoolClassRepository.findByTeacherID(teacherID);

        if (teacherClasses.isEmpty()) {
            throw new IllegalArgumentException("Dem Lehrer ist keine Klasse zugewiesen.");
        }

        List<Integer> classIDs = teacherClasses.stream()
                .map(SchoolClass::getClassID)
                .toList();

        return userRepository.findByClassIDInAndType(classIDs, UserType.STUDENT);
    }
}