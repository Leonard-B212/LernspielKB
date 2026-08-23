package de.lernspiel.level.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import de.lernspiel.level.config.bootstrap.LevelDefinitionProvider;
import de.lernspiel.level.dto.CreateLevelRequest;
import de.lernspiel.level.service.LevelService;

/**
 * Legt beim Start der Anwendung die fest definierten Standardlevel an.
 *
 * Alle registrierten LevelDefinitionProvider werden automatisch verarbeitet.
 * Bereits vorhandene Level werden nicht erneut angelegt.
 */
@Component
public class LevelBootstrap implements CommandLineRunner {

    private final LevelService levelService;
    private final List<LevelDefinitionProvider> levelDefinitionProviders;

    // Initialisiert den Bootstrap mit Level-Service und allen registrierten Level-Providern.
    public LevelBootstrap(LevelService levelService, List<LevelDefinitionProvider> levelDefinitionProviders) {
        this.levelService = levelService;
        this.levelDefinitionProviders = levelDefinitionProviders;
    }

    // Prüft beim Anwendungsstart alle definierten Level und legt fehlende Level an.
    @Override
    public void run(String... args) {
        int createdLevels = levelDefinitionProviders
                .stream()
                .flatMap(provider -> provider.createLevels().stream())
                .mapToInt(this::createLevelIfMissing)
                .sum();

        if (createdLevels == 0) {
            System.out.println("Level bootstrap: all levels already exist.");
        } else {
            System.out.println("Level bootstrap: created " + createdLevels + " new level(s).");
        }
    }

    // Legt ein Level nur an, wenn für Sprache, Kategorie und Levelnummer noch kein Level existiert.
    private int createLevelIfMissing(CreateLevelRequest request) {
        boolean exists = levelService.levelExists(
                request.getLanguage(),
                request.getCategory(),
                request.getLevelNumber()
        );

        if (exists) {
            return 0;
        }

        levelService.createLevel(request);
        return 1;
    }
}