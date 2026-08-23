package de.lernspiel.auth.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lernspiel.auth.dto.SchoolClassRequest;
import de.lernspiel.auth.dto.SchoolClassResponse;
import de.lernspiel.auth.entity.SchoolClass;
import de.lernspiel.auth.service.SchoolClassService;
import jakarta.validation.Valid;

/**
 * REST-Controller für die Verwaltung von Schulklassen.
 *
 * Ermöglicht das Anlegen und Laden von Klassen und stellt die Daten
 * über SchoolClassResponse-DTOs für das Frontend bereit.
 */
@RestController
@RequestMapping("/api/classes")
public class SchoolClassController {

    @Autowired
    private SchoolClassService schoolClassService;

    // Wandelt eine SchoolClass-Entity in das entsprechende Response-DTO um.
    private SchoolClassResponse mapToSchoolClassResponse(SchoolClass schoolClass) {
        return new SchoolClassResponse(
                schoolClass.getClassID(),
                schoolClass.getClassName(),
                schoolClass.getTeacherID()
        );
    }

    // Erstellt eine neue Schulklasse.
    @PostMapping
    public ResponseEntity<?> createClass(@RequestBody @Valid SchoolClassRequest req) {
        try {
            SchoolClass schoolClass = new SchoolClass();
            schoolClass.setClassName(req.getClassName());
            schoolClass.setTeacherID(req.getTeacherID());

            SchoolClass savedClass = schoolClassService.addClass(schoolClass);

            return ResponseEntity.ok(mapToSchoolClassResponse(savedClass));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // Gibt alle Schulklassen zurück.
    @GetMapping
    public ResponseEntity<List<SchoolClassResponse>> getAllClasses() {
        List<SchoolClassResponse> classes = schoolClassService
                .getAllClasses()
                .stream()
                .map(this::mapToSchoolClassResponse)
                .toList();

        return ResponseEntity.ok(classes);
    }

    // Gibt eine Schulklasse anhand ihrer ID zurück.
    @GetMapping("/{classID}")
    public ResponseEntity<?> getClassById(@PathVariable int classID) {
        Optional<SchoolClass> optionalClass = schoolClassService.getClassById(classID);

        if (optionalClass.isEmpty()) {
            return ResponseEntity.status(404).body("Klasse nicht gefunden.");
        }

        return ResponseEntity.ok(mapToSchoolClassResponse(optionalClass.get()));
    }
}