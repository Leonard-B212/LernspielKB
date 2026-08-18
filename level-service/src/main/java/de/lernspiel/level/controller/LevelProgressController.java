package de.lernspiel.level.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.lernspiel.level.dto.LevelProgressResponse;
import de.lernspiel.level.service.LevelProgressService;

/**
 * REST-Controller für den Fortschritt eines Benutzers.
 *
 * Die UserID wird aus dem aktuell authentifizierten Benutzer übernommen
 * und nicht vom Frontend übermittelt.
 *
 * Der Controller stellt dauerhaft die Schnittstelle zum Level-Fortschritt dar.
 * Der aktuelle Complete-Endpunkt dient zunächst zum Testen und kann später
 * durch die echte Ergebnisprüfung ersetzt werden.
 */
@RestController
@RequestMapping("/api/progress")
public class LevelProgressController {

    private final LevelProgressService levelProgressService;


    public LevelProgressController(
            LevelProgressService levelProgressService) {

        this.levelProgressService = levelProgressService;
    }


    /**
     * Liefert alle Level, die der aktuell angemeldete Benutzer abgeschlossen hat.
     */
    @GetMapping("/completed-levels")
    public ResponseEntity<LevelProgressResponse> getCompletedLevels(
            Principal principal) {

        Integer userID =
                getUserID(principal);

        List<Integer> completedLevelIDs =
                levelProgressService
                        .getCompletedLevelIDs(userID);

        return ResponseEntity.ok(
                new LevelProgressResponse(
                        completedLevelIDs
                )
        );
    }


    /**
     * Markiert ein Level testweise als abgeschlossen.
     *
     * Dieser Endpoint simuliert aktuell eine erfolgreiche Level-Prüfung.
     * Später soll completeLevel() von der echten Ergebnisprüfung aufgerufen werden.
     */
    @PostMapping("/levels/{levelID}/complete")
    public ResponseEntity<Void> completeLevel(
            @PathVariable Integer levelID,
            Principal principal) {

        Integer userID =
                getUserID(principal);

        levelProgressService.completeLevel(
                userID,
                levelID
        );

        return ResponseEntity.noContent().build();
    }


    /**
     * Liest die UserID aus dem authentifizierten Principal.
     */
    private Integer getUserID(Principal principal) {

        if (principal == null) {
            throw new IllegalStateException(
                    "Kein authentifizierter Benutzer vorhanden."
            );
        }

        return Integer.parseInt(
                principal.getName()
        );
    }
}