package de.lernspiel.auth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lernspiel.auth.entity.SchoolClass;
import de.lernspiel.auth.entity.User;
import de.lernspiel.auth.entity.UserType;
import de.lernspiel.auth.repository.SchoolClassRepository;
import de.lernspiel.auth.repository.UserRepository;

/*
 * Servicelogik für Schulklassen.
 */
@Service
public class SchoolClassService {

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private UserRepository userRepository;

    // Alle Schulklassen abrufen
    public List<SchoolClass> getAllClasses() {
        return schoolClassRepository.findAll();
    }

    // Schulklasse anhand ihrer ID abrufen
    public Optional<SchoolClass> getClassById(int classID) {
        return schoolClassRepository.findById(classID);
    }

    // Neue Schulklasse erstellen
    public SchoolClass addClass(SchoolClass schoolClass) {

        if (schoolClassRepository.existsByClassName(schoolClass.getClassName())) {
            throw new IllegalArgumentException("Eine Klasse mit diesem Namen existiert bereits.");
        }

        Optional<User> optionalTeacher =
                userRepository.findById(schoolClass.getTeacherID());

        if (optionalTeacher.isEmpty()) {
            throw new IllegalArgumentException("Der angegebene Lehrer existiert nicht.");
        }

        User teacher = optionalTeacher.get();

        if (teacher.getType() != UserType.TEACHER) {
            throw new IllegalArgumentException("Der angegebene Benutzer ist kein Lehrer.");
        }

        return schoolClassRepository.save(schoolClass);
    }
}