package de.lernspiel.level.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lernspiel.level.dto.CreateLevelRequest;
import de.lernspiel.level.dto.LevelResponse;
import de.lernspiel.level.service.LevelService;
import de.lernspiel.level.dto.LevelOverviewResponse;

/**
 * REST-Controller für Level-Daten.
 *
 * Ermöglicht das Laden und Anlegen von Leveln.
 */
@RestController
@RequestMapping("/api/levels")
public class LevelController {

    private final LevelService levelService;


    public LevelController(LevelService levelService) {
        this.levelService = levelService;
    }

    /**
     * Gibt alle verfügbaren Level in kompakter Form zurück.
     */
    @GetMapping
    public ResponseEntity<List<LevelOverviewResponse>> getAllLevels() {

        return ResponseEntity.ok(
                levelService.getAllLevels()
        );
    }


    /**
     * Gibt ein einzelnes Level inklusive seiner verfügbaren
     * Code-Komponenten zurück.
     */
    @GetMapping("/{levelID}")
    public ResponseEntity<?> getLevelById(
            @PathVariable Integer levelID) {

        try {
            
            LevelResponse level =
                    levelService.getLevelById(
                            levelID
                    );

            return ResponseEntity.ok(level);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(404)
                    .body(e.getMessage());
        }
    }


    /**
     * Legt ein neues Level inklusive seiner verfügbaren
     * Code-Komponenten an.
     */
    @PostMapping
    public ResponseEntity<?> createLevel(
            @RequestBody CreateLevelRequest request) {

        try {

            LevelResponse level =
                    levelService.createLevel(
                            request
                    );

            return ResponseEntity.ok(level);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}