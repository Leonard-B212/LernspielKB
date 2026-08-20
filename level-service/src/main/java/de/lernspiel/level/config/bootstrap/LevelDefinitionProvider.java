package de.lernspiel.level.config.bootstrap;

import java.util.List;

import de.lernspiel.level.dto.CreateLevelRequest;

/**
 * Liefert eine Gruppe fest definierter Level für den Level-Bootstrap.
 *
 * Neue Levelgruppen müssen lediglich dieses Interface implementieren
 * und als Spring-Komponente registriert werden.
 */
public interface LevelDefinitionProvider {

    List<CreateLevelRequest> createLevels();
}