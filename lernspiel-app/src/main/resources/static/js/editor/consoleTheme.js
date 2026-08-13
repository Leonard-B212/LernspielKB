/**
 * Verwaltet ausschließlich die Theme-Umschaltung
 * der Interpreter-Konsole.
 *
 * Das ausgewählte Theme wird als data-theme am Console-Element
 * gesetzt. Die eigentliche Darstellung übernimmt anschließend CSS.
 *
 * Dadurch bleibt die Theme-Logik unabhängig von Interpreter,
 * Sandbox und Editor.
 */


/**
 * Registriert die Theme-Buttons und synchronisiert Button,
 * Slider und data-theme der Interpreter-Konsole.
 */
export function initializeConsoleTheme({
    interpreterConsole,
    consoleThemeSwitch
}) {

    const options =
        consoleThemeSwitch.querySelectorAll(
            ".console-theme-option"
        );


    options.forEach((button) => {

        button.addEventListener(
            "click",
            () => {

                const selectedTheme =
                    button.dataset.theme;

                interpreterConsole.dataset.theme =
                    selectedTheme;

                consoleThemeSwitch.dataset.activeTheme =
                    selectedTheme;


                options.forEach((option) => {
                    option.classList.toggle(
                        "active",
                        option === button
                    );
                });
            }
        );
    });
}