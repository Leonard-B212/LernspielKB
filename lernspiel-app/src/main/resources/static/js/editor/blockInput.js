/**
 * Stellt die Eingabeoberflächen für konfigurierbare Code-Blöcke bereit.
 * Wird von blockFactory.js verwendet, damit Eingaben nicht über Browser-Prompts erfolgen.
 */

// Öffnet die Eingabe für einen Variablennamen.
export function requestVariableName() {
    return createTextDialog(
        "Variablenname",
        "Wie soll die Variable heißen?",
        "z. B. result"
    );
}

// Öffnet die Eingabe für einen typisierten Wert.
export function requestValue() {
    return new Promise((resolve) => {
        const overlay = createOverlay();
        const dialog = createDialog("Wert festlegen");

        let selectedType = "INT";
        let currentInput = null;

        const typeSelector = document.createElement("div");
        typeSelector.classList.add("block-input-types");

        const valueContainer = document.createElement("div");
        valueContainer.classList.add("block-input-value");

        const typeButtons = ["INT", "STRING", "BOOLEAN"].map((type) => {
            const button = document.createElement("button");

            button.type = "button";
            button.textContent = getTypeLabel(type);
            button.dataset.type = type;
            button.classList.add("block-input-type");
            button.classList.toggle("active", type === selectedType);

            button.addEventListener("click", () => {
                selectedType = type;

                typeButtons.forEach((typeButton) => {
                    typeButton.classList.toggle(
                        "active",
                        typeButton.dataset.type === selectedType
                    );
                });

                renderValueInput();
            });

            typeSelector.appendChild(button);
            return button;
        });

        // Baut das Eingabefeld passend zum aktuell gewählten Datentyp neu auf.
        function renderValueInput() {
            valueContainer.innerHTML = "";

            if (selectedType === "BOOLEAN") {
                currentInput = createBooleanInput();
                valueContainer.appendChild(currentInput.element);
                return;
            }

            const input = document.createElement("input");

            input.type = selectedType === "INT" ? "number" : "text";
            input.placeholder = selectedType === "INT" ? "z. B. 5" : "z. B. Hallo";
            input.classList.add("block-input-field");

            currentInput = {
                getValue: () => input.value
            };

            valueContainer.appendChild(input);
            requestAnimationFrame(() => input.focus());
        }

        const actions = createActions(
            () => {
                overlay.remove();
                resolve(null);
            },
            () => {
                const value = currentInput?.getValue();

                overlay.remove();
                resolve({
                    type: selectedType,
                    value
                });
            }
        );

        dialog.appendChild(typeSelector);
        dialog.appendChild(valueContainer);
        dialog.appendChild(actions);

        overlay.appendChild(dialog);
        document.body.appendChild(overlay);

        renderValueInput();
    });
}

// Öffnet ein einfaches Texteingabefenster und liefert den eingegebenen Wert zurück.
function createTextDialog(title, label, placeholder) {
    return new Promise((resolve) => {
        const overlay = createOverlay();
        const dialog = createDialog(title);

        const inputLabel = document.createElement("label");
        inputLabel.classList.add("block-input-label");
        inputLabel.textContent = label;

        const input = document.createElement("input");
        input.type = "text";
        input.placeholder = placeholder;
        input.classList.add("block-input-field");

        const actions = createActions(
            () => {
                overlay.remove();
                resolve(null);
            },
            () => {
                overlay.remove();
                resolve(input.value);
            }
        );

        input.addEventListener("keydown", (event) => {
            if (event.key === "Enter") {
                actions.querySelector(".confirm").click();
            }
        });

        dialog.appendChild(inputLabel);
        dialog.appendChild(input);
        dialog.appendChild(actions);

        overlay.appendChild(dialog);
        document.body.appendChild(overlay);

        requestAnimationFrame(() => input.focus());
    });
}

// Erstellt die true/false-Auswahl für Boolean-Werte.
function createBooleanInput() {
    const container = document.createElement("div");
    container.classList.add("block-input-boolean");

    let value = true;

    ["true", "false"].forEach((option) => {
        const button = document.createElement("button");

        button.type = "button";
        button.textContent = option;
        button.classList.add("block-input-boolean-option");
        button.classList.toggle("active", option === "true");

        button.addEventListener("click", () => {
            value = option === "true";

            container.querySelectorAll(".block-input-boolean-option").forEach((booleanButton) => {
                booleanButton.classList.toggle("active", booleanButton === button);
            });
        });

        container.appendChild(button);
    });

    return {
        element: container,
        getValue: () => value
    };
}

// Erstellt das Overlay für die Block-Eingabe.
function createOverlay() {
    const overlay = document.createElement("div");
    overlay.classList.add("block-input-overlay");

    return overlay;
}

// Erstellt das Dialogelement mit Überschrift.
function createDialog(title) {
    const dialog = document.createElement("div");
    dialog.classList.add("block-input-dialog");

    const heading = document.createElement("h3");
    heading.textContent = title;

    dialog.appendChild(heading);

    return dialog;
}

// Erstellt die gemeinsamen Abbrechen- und Übernehmen-Buttons.
function createActions(onCancel, onConfirm) {
    const actions = document.createElement("div");
    actions.classList.add("block-input-actions");

    const cancelButton = document.createElement("button");
    cancelButton.type = "button";
    cancelButton.textContent = "Abbrechen";
    cancelButton.classList.add("secondary");

    const confirmButton = document.createElement("button");
    confirmButton.type = "button";
    confirmButton.textContent = "Übernehmen";
    confirmButton.classList.add("confirm");

    cancelButton.addEventListener("click", onCancel);
    confirmButton.addEventListener("click", onConfirm);

    actions.appendChild(cancelButton);
    actions.appendChild(confirmButton);

    return actions;
}

// Liefert die sichtbare Bezeichnung eines Datentyps.
function getTypeLabel(type) {
    const labels = {
        INT: "int",
        STRING: "String",
        BOOLEAN: "boolean"
    };

    return labels[type] ?? type;
}