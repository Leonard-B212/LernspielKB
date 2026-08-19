/**
 * Einstiegspunkt der Skilltree-Seite.
 *
 * Lädt verfügbare Level und den Fortschritt des Benutzers
 * und verbindet State und Renderer miteinander.
 */

import {
    getAllLevels
} from "../api/levelApi.js";

import {
    getCompletedLevels
} from "../api/progressApi.js";

import {
    createSkilltreeState
} from "../skilltree/skilltreeState.js";

import {
    createSkilltreeRenderer
} from "../skilltree/skilltreeRenderer.js";


const skilltreeElement =
    document.getElementById(
        "skilltree"
    );

const languageSelector =
    document.getElementById(
        "language-selector"
    );

const message =
    document.getElementById(
        "skilltree-message"
    );


const state =
    createSkilltreeState();


const renderer =
    createSkilltreeRenderer({

        skilltreeElement,

        languageSelector,

        onLanguageChange:
            handleLanguageChange,

        onLevelSelect:
            handleLevelSelect
    });


function handleLanguageChange(
    language) {

    state.setActiveLanguage(
        language
    );

    render();
}


function handleLevelSelect(
    level) {

    window.location.href =
        `/level.html?id=${level.levelID}`;
}


function render() {

    renderer.renderLanguageSelector(
        state.getLanguages(),
        state.getActiveLanguage()
    );


    renderer.renderLevels(
        state.getLevelsForActiveLanguage(),
        levelID =>
            state.isCompleted(
                levelID
            )
    );
}


/**
 * Lädt alle benötigten Daten und initialisiert den Skilltree.
 */
async function initialize() {

    try {

        const [
            levels,
            progress
        ] =
            await Promise.all([
                getAllLevels(),
                getCompletedLevels()
            ]);


        state.setLevels(
            levels
        );

        state.setCompletedLevelIDs(
            progress.completedLevelIDs
        );


        const languages =
            state.getLanguages();


        if (languages.length > 0) {

            state.setActiveLanguage(
                languages[0]
            );
        }


        render();

    }
    catch (error) {

        console.error(
            "Skilltree konnte nicht initialisiert werden:",
            error
        );

        message.textContent =
            error.message;
    }
}


initialize();