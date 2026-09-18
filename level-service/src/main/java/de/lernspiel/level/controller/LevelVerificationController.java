package de.lernspiel.level.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lernspiel.level.dto.LevelVerificationRequest;
import de.lernspiel.level.service.LevelVerificationService;

/**
 * REST-Controller für die Validierung von Programmcode gegen die tatsächlich gestellten Anforderungen eines Levels.
 *
 * Der Controller nimmt Anfragen zur Levelprüfung in form eines LevelVerificationRequests entgegen.
 * Diese Anfrage wird zur Verarbeitung an den LevelVerficationService weitergeleitet 
 * und das Ergebnis wird in Form eines Wahrheitswerts an das Frontend zurückgegeben.
 */
@RestController
@RequestMapping("/api/levelVerification")
public class LevelVerificationController {

    private final LevelVerificationService levelVerificationService;

    public LevelVerificationController(LevelVerificationService levelVerificationService) {
        this.levelVerificationService = levelVerificationService;
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verify(@RequestBody LevelVerificationRequest request) {
        boolean successful = levelVerificationService.verify(
                request.getLevelID(),
                request.getActualExecutionLog()
        );

        return ResponseEntity.ok(successful);
    }
}