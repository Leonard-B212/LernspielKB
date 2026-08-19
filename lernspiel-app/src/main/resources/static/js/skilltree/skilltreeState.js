/**
 * Verwaltet den Zustand der Skilltree-Seite.
 *
 * Enthält die verfügbaren Level, abgeschlossene Level
 * und die aktuell ausgewählte Programmiersprache.
 */
export function createSkilltreeState() {

    let levels = [];
    let completedLevelIDs = [];
    let activeLanguage = null;


    function setLevels(newLevels) {
        levels = newLevels;
    }


    function getLevels() {
        return levels;
    }


    function setCompletedLevelIDs(ids) {
        completedLevelIDs = ids;
    }


    function getCompletedLevelIDs() {
        return completedLevelIDs;
    }


    function setActiveLanguage(language) {
        activeLanguage = language;
    }


    function getActiveLanguage() {
        return activeLanguage;
    }


    // Liefert alle Sprachen, für die mindestens ein Level existiert.
    function getLanguages() {

        return [
            ...new Set(
                levels.map(
                    level => level.language
                )
            )
        ];
    }


    // Liefert nur die Level der aktuell ausgewählten Sprache.
    function getLevelsForActiveLanguage() {

        return levels.filter(
            level =>
                level.language === activeLanguage
        );
    }


    function isCompleted(levelID) {

        return completedLevelIDs.includes(
            levelID
        );
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