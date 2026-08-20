package de.lernspiel.level.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.lernspiel.level.dto.CreateLevelRequest;
import de.lernspiel.level.dto.LevelComponentRequest;
import de.lernspiel.level.dto.LevelComponentResponse;
import de.lernspiel.level.dto.LevelOverviewResponse;
import de.lernspiel.level.dto.LevelResponse;
import de.lernspiel.level.entity.Component;
import de.lernspiel.level.entity.Level;
import de.lernspiel.level.entity.LevelComponent;
import de.lernspiel.level.entity.ProgrammingLanguage;
import de.lernspiel.level.repository.ComponentRepository;
import de.lernspiel.level.repository.LevelComponentRepository;
import de.lernspiel.level.repository.LevelRepository;
import de.lernspiel.level.repository.ProgrammingLanguageRepository;
import de.lernspiel.level.entity.LevelCategory;
import de.lernspiel.level.repository.LevelCategoryRepository;

/**
 * Service für das Laden und Anlegen von Level-Daten.
 *
 * Der Service verarbeitet Level, Programmiersprachen und die zugehörigen
 * Code-Komponenten und wandelt die Entities in DTOs für das Frontend um.
 */
@Service
public class LevelService {

    private final LevelRepository levelRepository;
    private final LevelComponentRepository levelComponentRepository;
    private final ComponentRepository componentRepository;
    private final ProgrammingLanguageRepository programmingLanguageRepository;
    private final LevelCategoryRepository levelCategoryRepository;

    public LevelService(
                LevelRepository levelRepository,
                LevelComponentRepository levelComponentRepository,
                ComponentRepository componentRepository,
                ProgrammingLanguageRepository programmingLanguageRepository,
                LevelCategoryRepository levelCategoryRepository) {

        this.levelRepository = levelRepository;
        this.levelComponentRepository = levelComponentRepository;
        this.componentRepository = componentRepository;
        this.programmingLanguageRepository = programmingLanguageRepository;
        this.levelCategoryRepository = levelCategoryRepository;
        }


    /**
     * Lädt ein Level anhand seiner ID inklusive der verfügbaren Komponenten.
     */
    public LevelResponse getLevelById(Integer levelID) {

        Level level = levelRepository
                .findById(levelID)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Level mit ID " + levelID + " wurde nicht gefunden."
                        )
                );


        List<LevelComponentResponse> componentResponses =
                levelComponentRepository
                        .findByLevelLevelID(levelID)
                        .stream()
                        .map(this::mapToComponentResponse)
                        .toList();


        return mapToLevelResponse(
                level,
                componentResponses
        );
    }


    /**
     * Erstellt ein neues Level inklusive Sprache und verfügbarer Komponenten.
     *
     * Noch nicht vorhandene Programmiersprachen und Components werden
     * automatisch angelegt.
     */
    @Transactional
    public LevelResponse createLevel(CreateLevelRequest request) {

        ProgrammingLanguage language =
                getOrCreateLanguage(request.getLanguage());
        LevelCategory category =
        getOrCreateCategory(
                request.getCategory(),
                request.getCategoryOrder()
        );

        if (
            levelRepository
                .existsByCategoryAndLevelNumberAndLanguage(
                        category,
                        request.getLevelNumber(),
                        language
    )
        ) {
            throw new IllegalArgumentException(
                    "Für Kategorie "
                    + request.getCategory()
                    + ", Level "
                    + request.getLevelNumber()
                    + " und Sprache "
                    + language.getLanguageName()
                    + " existiert bereits ein Level."
            );
        }


        Level level = new Level();

        level.setLevelName(
                request.getLevelName()
        );

        level.setLevelDescription(
                request.getLevelDescription()
        );

        level.setCategory(
                category
        );

        level.setLevelNumber(
                request.getLevelNumber()
        );

        level.setLanguage(
                language
        );


        Level savedLevel =
                levelRepository.save(level);


        List<LevelComponentResponse> componentResponses =
                new ArrayList<>();


        if (request.getComponents() != null) {

            for (
                LevelComponentRequest componentRequest
                : request.getComponents()
            ) {

                if (
                    componentRequest.getAmount() == null
                    || componentRequest.getAmount() <= 0
                ) {
                    throw new IllegalArgumentException(
                            "ComponentAmount muss größer als 0 sein."
                    );
                }


                Component component =
                        getOrCreateComponent(
                                componentRequest
                                    .getType()
                        );


                LevelComponent levelComponent =
                        new LevelComponent();

                levelComponent.setLevel(
                        savedLevel
                );

                levelComponent.setComponent(
                        component
                );

                levelComponent.setComponentAmount(
                        componentRequest.getAmount()
                );


                levelComponentRepository.save(
                        levelComponent
                );


                componentResponses.add(
                        new LevelComponentResponse(
                                component.getComponentType(),
                                componentRequest.getAmount()
                        )
                );
            }
        }


        return mapToLevelResponse(
                savedLevel,
                componentResponses
        );
    }


    /**
     * Lädt eine Programmiersprache oder legt sie an, falls sie noch nicht existiert.
     */
    private ProgrammingLanguage getOrCreateLanguage(
            String languageName) {

        if (
            languageName == null
            || languageName.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Programmiersprache darf nicht leer sein."
            );
        }


        String normalizedLanguage =
                languageName
                        .trim()
                        .toUpperCase();


        return programmingLanguageRepository
                .findByLanguageName(normalizedLanguage)
                .orElseGet(() -> {

                    ProgrammingLanguage language =
                            new ProgrammingLanguage();

                    language.setLanguageName(
                            normalizedLanguage
                    );

                    return programmingLanguageRepository
                            .save(language);
                });
    }

        /**
         * Lädt eine Level-Kategorie oder legt sie an, falls sie noch nicht existiert.
         */
        private LevelCategory getOrCreateCategory(
                String categoryName,
                Integer categoryOrder) {

        if (
                categoryName == null
                || categoryName.isBlank()
        ) {
                throw new IllegalArgumentException(
                        "Kategorie darf nicht leer sein."
                );
        }

        String normalizedCategory =
                categoryName
                        .trim()
                        .toUpperCase();

        return levelCategoryRepository
                .findByCategoryName(normalizedCategory)
                .orElseGet(() -> {

                        if (categoryOrder == null) {
                        throw new IllegalArgumentException(
                                "Für eine neue Kategorie muss categoryOrder angegeben werden."
                        );
                        }

                        LevelCategory category =
                                new LevelCategory();

                        category.setCategoryName(
                                normalizedCategory
                        );

                        category.setCategoryOrder(
                                categoryOrder
                        );

                        return levelCategoryRepository
                                .save(category);
                });
        }


    /**
     * Lädt eine Component oder legt sie anhand des gemeinsamen CodeType neu an.
     */
    private Component getOrCreateComponent(
            de.lernspiel.common.code.CodeType type) {

        if (type == null) {
            throw new IllegalArgumentException(
                    "ComponentType darf nicht leer sein."
            );
        }


        return componentRepository
                .findByComponentType(type)
                .orElseGet(() -> {

                    Component component =
                            new Component();

                    component.setComponentType(
                            type
                    );

                    return componentRepository
                            .save(component);
                });
    }


    /**
     * Wandelt eine LevelComponent-Entity in das entsprechende Response-DTO um.
     */
    private LevelComponentResponse mapToComponentResponse(
            LevelComponent levelComponent) {

        return new LevelComponentResponse(
                levelComponent
                        .getComponent()
                        .getComponentType(),

                levelComponent
                        .getComponentAmount()
        );
    }


    /**
     * Erstellt aus Level und Components den vollständigen Response fürs Frontend.
     */
    private LevelResponse mapToLevelResponse(
            Level level,
            List<LevelComponentResponse> components) {

        return new LevelResponse(
                level.getLevelID(),
                level.getLevelName(),
                level.getLevelDescription(),
                level.getCategory().getCategoryID(),
                level.getCategory().getCategoryName(),
                level.getCategory().getCategoryOrder(),
                level.getLevelNumber(),
                level.getLanguage().getLanguageID(),
                level.getLanguage().getLanguageName(),
                components
        );
    }

    /**
     * Liefert alle vorhandenen Level in kompakter Form für Übersichtsseiten.
     *
     * Die Level werden nach Programmiersprache, Kategorie und Levelnummer sortiert.
     */
    public List<LevelOverviewResponse> getAllLevels() {

        return levelRepository
                .findAll()
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        (Level level) ->
                                                level.getLanguage()
                                                        .getLanguageName()
                                )
                                .thenComparing(
                                        level ->
                                                level.getCategory()
                                                        .getCategoryOrder()
                                )
                                .thenComparing(
                                        Level::getLevelNumber
                                )
                )
                .map(this::mapToOverviewResponse)
                .toList();
    }


    /**
     * Wandelt ein Level in die kompakte Darstellung für Übersichtsseiten um.
     */
    private LevelOverviewResponse mapToOverviewResponse(
            Level level) {

        return new LevelOverviewResponse(
                level.getLevelID(),
                level.getLevelName(),
                level.getCategory().getCategoryID(),
                level.getCategory().getCategoryName(),
                level.getCategory().getCategoryOrder(),
                level.getLevelNumber(),
                level.getLanguage().getLanguageID(),
                level.getLanguage().getLanguageName()
        );
    }

        /**
         * Prüft, ob für eine Kombination aus Sprache,
         * Kategorie und Levelnummer bereits ein Level existiert.
         */
        public boolean levelExists(
                String languageName,
                String categoryName,
                Integer levelNumber) {

        if (
                languageName == null
                || categoryName == null
                || levelNumber == null
        ) {
                return false;
        }


        String normalizedLanguage =
                languageName
                        .trim()
                        .toUpperCase();

        String normalizedCategory =
                categoryName
                        .trim()
                        .toUpperCase();


        ProgrammingLanguage language =
                programmingLanguageRepository
                        .findByLanguageName(
                                normalizedLanguage
                        )
                        .orElse(null);


        LevelCategory category =
                levelCategoryRepository
                        .findByCategoryName(
                                normalizedCategory
                        )
                        .orElse(null);


        if (
                language == null
                || category == null
        ) {
                return false;
        }


        return levelRepository
                .existsByCategoryAndLevelNumberAndLanguage(
                        category,
                        levelNumber,
                        language
                );
        }
}