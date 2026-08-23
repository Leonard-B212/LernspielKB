/**
 * Verwaltet den Zustand der Skilltree-Seite.
 *
 * Enthält die verfügbaren Level, abgeschlossene Level
 * und die aktuell ausgewählte Programmiersprache.
 */

// Erstellt einen unabhängigen State für die Skilltree-Seite.
export function createSkilltreeState() {
    let levels = [];
    let completedLevelIDs = [];
    let activeLanguage = null;

    // Setzt die verfügbaren Level.
    function setLevels(newLevels) {
        levels = newLevels;
    }

    // Liefert alle verfügbaren Level.
    function getLevels() {
        return levels;
    }

    // Setzt die IDs der abgeschlossenen Level.
    function setCompletedLevelIDs(ids) {
        completedLevelIDs = ids;
    }

    // Liefert die IDs der abgeschlossenen Level.
    function getCompletedLevelIDs() {
        return completedLevelIDs;
    }

    // Setzt die aktuell ausgewählte Programmiersprache.
    function setActiveLanguage(language) {
        activeLanguage = language;
    }

    // Liefert die aktuell ausgewählte Programmiersprache.
    function getActiveLanguage() {
        return activeLanguage;
    }

    // Liefert alle Sprachen, für die mindestens ein Level existiert.
    function getLanguages() {
        return [...new Set(levels.map(level => level.language))];
    }

    // Liefert nur die Level der aktuell ausgewählten Sprache.
    function getLevelsForActiveLanguage() {
        return levels.filter(level => level.language === activeLanguage);
    }

    // Prüft, ob ein Level bereits abgeschlossen wurde.
    function isCompleted(levelID) {
        return completedLevelIDs.includes(levelID);
    }

    return {
        setLevels,
        getLevels,
        setCompletedLevelIDs,
        getCompletedLevelIDs,
        setActiveLanguage,
        getActiveLanguage,
        getLanguages,
        getLevelsForActiveLanguage,
        isCompleted
    };
}