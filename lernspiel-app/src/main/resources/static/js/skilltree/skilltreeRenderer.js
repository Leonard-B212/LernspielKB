/**
 * Rendert die visuelle Struktur des Skilltrees.
 *
 * Kategorien bilden den vertikalen Hauptpfad.
 * Die zugehörigen Level werden fächerförmig um ihre Kategorie angeordnet.
 *
 * Ein SVG-Layer zeichnet die Verbindungen zwischen den Nodes.
 * Die Verbindung zwischen Kategorien ist getrennt von den Level-Verbindungen,
 * damit später beispielsweise Fortschritt dargestellt werden kann.
 */

import {
    initializeSkilltreePhysics
} from "./skilltreePhysics.js";

export function createSkilltreeRenderer({
    skilltreeElement,
    languageSelector,
    onLanguageChange,
    onLevelSelect
}) {

    let resizeHandlerRegistered = false;


    /**
     * Rendert die verfügbaren Programmiersprachen.
     */
    function renderLanguageSelector(
        languages,
        activeLanguage) {

        languageSelector.innerHTML = "";


        languages.forEach(language => {

            const button =
                document.createElement(
                    "button"
                );

            button.type = "button";

            button.textContent =
                language;

            button.classList.add(
                "language-option"
            );

            button.classList.toggle(
                "active",
                language === activeLanguage
            );

            button.addEventListener(
                "click",
                () =>
                    onLanguageChange(
                        language
                    )
            );

            languageSelector.appendChild(
                button
            );
        });
    }


    /**
     * Rendert alle Kategorien und deren Level-Nodes.
     */

    function renderLevels(
        levels,
        isCompleted) {

        skilltreeElement.innerHTML = "";


        // SVG liegt hinter allen Nodes.
        const svg =
            createConnectionLayer();

        skilltreeElement.appendChild(
            svg
        );


        const categories =
            groupByCategory(levels);


        categories.forEach(category => {

            const categoryBranch =
                createCategoryBranch(
                    category,
                    isCompleted
                );

            skilltreeElement.appendChild(
                categoryBranch
            );
        });


        /*
        * Erst nachdem alle Nodes im DOM liegen,
        * können Verbindungen und Physics gestartet werden.
        */
        requestAnimationFrame(() => {

            drawConnections();

            initializeSkilltreePhysics({
                skilltreeElement,

                // Linien werden während der Bewegung aktualisiert.
                onUpdate:
                    drawConnections
            });
        });


        /*
        * Bei einer Größenänderung müssen nur die
        * Verbindungslinien neu berechnet werden.
        */
        if (!resizeHandlerRegistered) {

            window.addEventListener(
                "resize",
                () =>
                    requestAnimationFrame(
                        drawConnections
                    )
            );

            resizeHandlerRegistered = true;
        }
    }


    /**
     * Erstellt einen transparenten SVG-Layer für alle Verbindungen.
     */
    function createConnectionLayer() {

        const svg =
            document.createElementNS(
                "http://www.w3.org/2000/svg",
                "svg"
            );

        svg.classList.add(
            "skilltree-connections"
        );

        svg.setAttribute(
            "aria-hidden",
            "true"
        );

        return svg;
    }


    /**
     * Erstellt den Bereich einer einzelnen Kategorie.
     */
    function createCategoryBranch(
        category,
        isCompleted) {

        const branch =
            document.createElement(
                "section"
            );

        branch.classList.add(
            "skilltree-branch"
        );

        branch.dataset.categoryId =
            category.id;


        const categoryNode =
            createCategoryNode(
                category
            );

        branch.appendChild(
            categoryNode
        );


        const levelContainer =
            document.createElement(
                "div"
            );

        levelContainer.classList.add(
            "skilltree-levels"
        );


        category.levels.forEach(
            (level, index) => {

                const levelNode =
                    createLevelNode(
                        level,
                        isCompleted(
                            level.levelID
                        )
                    );


                positionLevelNode(
                    levelNode,
                    index,
                    category.levels.length
                );


                levelContainer.appendChild(
                    levelNode
                );
            }
        );


        branch.appendChild(
            levelContainer
        );


        return branch;
    }


    /**
     * Erstellt den großen zentralen Node einer Kategorie.
     */
    function createCategoryNode(
        category) {

        const node =
            document.createElement(
                "div"
            );

        node.classList.add(
            "skilltree-category-node"
        );

        node.dataset.categoryId =
            category.id;

        node.dataset.categoryOrder =
            category.order;


        const name =
            document.createElement(
                "span"
            );

        name.classList.add(
            "skilltree-category-name"
        );

        name.textContent =
            category.name;


        node.appendChild(
            name
        );


        return node;
    }


    /**
     * Erstellt einen einzelnen anklickbaren Level-Node.
     */
    function createLevelNode(
        level,
        completed) {

        const button =
            document.createElement(
                "button"
            );

        button.type = "button";

        button.classList.add(
            "skilltree-level-node"
        );


        if (completed) {
            button.classList.add(
                "completed"
            );
        }


        button.dataset.levelId =
            level.levelID;

        button.dataset.levelNumber =
            level.levelNumber;


        const circle =
            document.createElement(
                "span"
            );

        circle.classList.add(
            "skilltree-level-circle"
        );


        const number =
            document.createElement(
                "span"
            );

        number.classList.add(
            "skilltree-level-number"
        );

        number.textContent =
            level.levelNumber;


        const label =
            document.createElement(
                "span"
            );

        label.classList.add(
            "skilltree-level-label"
        );

        label.textContent =
            level.levelName;


        circle.appendChild(
            number
        );

        button.appendChild(
            circle
        );

        button.appendChild(
            label
        );


        button.addEventListener(
            "click",
            () =>
                onLevelSelect(
                    level
                )
        );


        /*
        * Während der Hover-Animation bewegt sich der Node leicht.
        * Die SVG-Verbindung wird deshalb währenddessen neu gezeichnet.
        */
        let hoverAnimationFrame = null;


        function updateConnectionsWhileHovered() {

            drawConnections();

            hoverAnimationFrame =
                requestAnimationFrame(
                    updateConnectionsWhileHovered
                );
        }


        button.addEventListener(
            "mouseenter",
            () => {

                if (hoverAnimationFrame !== null) {
                    return;
                }

                updateConnectionsWhileHovered();
            }
        );


        button.addEventListener(
            "mouseleave",
            () => {

                if (hoverAnimationFrame !== null) {

                    cancelAnimationFrame(
                        hoverAnimationFrame
                    );

                    hoverAnimationFrame = null;
                }


                // Nach Ende der Animation einmal die finale Position zeichnen.
                requestAnimationFrame(
                    drawConnections
                );
            }
        );

        return button;
    }


    /**
     * Verteilt die Level eines Branches in einem flachen Fächer.
     *
     * Die Position wird über CSS-Variablen gespeichert,
     * damit eine spätere Physics-Schicht darauf aufbauen kann.
     */
    function positionLevelNode(
        node,
        index,
        total) {

        const center =
            (total - 1) / 2;

        const relativeIndex =
            index - center;


        /*
         * Horizontaler Abstand der Nodes.
         * Bei vielen Leveln wird der Abstand etwas kompakter.
         */
        const spacing =
            total <= 3
                ? 185
                : 165;

        const x =
            relativeIndex * spacing;

        const y =
            65
            + Math.abs(relativeIndex) * 38;


        node.style.setProperty(
            "--node-x",
            `${x}px`
        );

        node.style.setProperty(
            "--node-y",
            `${y}px`
        );
    }


    /**
     * Zeichnet sämtliche Verbindungen anhand der aktuellen DOM-Positionen neu.
     */
    function drawConnections() {

        const svg =
            skilltreeElement.querySelector(
                ".skilltree-connections"
            );


        if (!svg) {
            return;
        }


        svg.innerHTML = "";


        const branches =
            [
                ...skilltreeElement
                    .querySelectorAll(
                        ".skilltree-branch"
                    )
            ];


        branches.forEach(
            (branch, index) => {

                const categoryNode =
                    branch.querySelector(
                        ".skilltree-category-node"
                    );


                const levelNodes =
                    branch.querySelectorAll(
                        ".skilltree-level-node"
                    );


                // Kategorie → einzelne Level.
                levelNodes.forEach(
                    levelNode => {

                        drawConnection(
                            svg,
                            categoryNode,
                            levelNode,
                            "skilltree-level-connection"
                        );
                    }
                );


                // Kategorie → nächste Kategorie.
                const nextBranch =
                    branches[index + 1];


                if (nextBranch) {

                    const nextCategoryNode =
                        nextBranch.querySelector(
                            ".skilltree-category-node"
                        );


                    drawConnection(
                        svg,
                        categoryNode,
                        nextCategoryNode,
                        "skilltree-category-connection"
                    );
                }
            }
        );
    }


    /**
     * Zeichnet eine einzelne Linie zwischen zwei DOM-Elementen.
     */
    function drawConnection(
        svg,
        fromElement,
        toElement,
        cssClass) {

        if (
            !fromElement
            || !toElement
        ) {
            return;
        }


        const treeRect =
            skilltreeElement
                .getBoundingClientRect();

        const fromRect =
            fromElement
                .getBoundingClientRect();

        const toRect =
            toElement
                .getBoundingClientRect();


        const x1 =
            fromRect.left
            + fromRect.width / 2
            - treeRect.left;

        const y1 =
            fromRect.top
            + fromRect.height / 2
            - treeRect.top;

        const x2 =
            toRect.left
            + toRect.width / 2
            - treeRect.left;

        const y2 =
            toRect.top
            + toRect.height / 2
            - treeRect.top;


        const line =
            document.createElementNS(
                "http://www.w3.org/2000/svg",
                "line"
            );


        line.setAttribute(
            "x1",
            x1
        );

        line.setAttribute(
            "y1",
            y1
        );

        line.setAttribute(
            "x2",
            x2
        );

        line.setAttribute(
            "y2",
            y2
        );


        line.classList.add(
            cssClass
        );


        svg.appendChild(
            line
        );
    }


    return {
        renderLanguageSelector,
        renderLevels
    };
}


/**
 * Gruppiert Level anhand ihrer Kategorie und sortiert
 * Kategorien sowie Level anhand ihrer definierten Reihenfolge.
 */
function groupByCategory(levels) {

    const categoryMap =
        new Map();


    levels.forEach(level => {

        if (
            !categoryMap.has(
                level.categoryID
            )
        ) {

            categoryMap.set(
                level.categoryID,
                {
                    id:
                        level.categoryID,

                    name:
                        level.category,

                    order:
                        level.categoryOrder,

                    levels: []
                }
            );
        }


        categoryMap
            .get(
                level.categoryID
            )
            .levels
            .push(
                level
            );
    });


    const categories =
        [...categoryMap.values()];


    categories.sort(
        (a, b) =>
            a.order - b.order
    );


    categories.forEach(category => {

        category.levels.sort(
            (a, b) =>
                a.levelNumber
                - b.levelNumber
        );
    });


    return categories;
}