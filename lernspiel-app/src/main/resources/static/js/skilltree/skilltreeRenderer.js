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

import { initializeSkilltreePhysics } from "./skilltreePhysics.js";

// Erstellt den Renderer für Sprachauswahl, Kategorien, Level und Verbindungen des Skilltrees.
export function createSkilltreeRenderer({
    skilltreeElement,
    languageSelector,
    onLanguageChange,
    onLevelSelect
}) {
    let resizeHandlerRegistered = false;

    // Rendert die verfügbaren Programmiersprachen und markiert die aktive Sprache.
    function renderLanguageSelector(languages, activeLanguage) {
        languageSelector.innerHTML = "";

        languages.forEach((language) => {
            const button = document.createElement("button");

            button.type = "button";
            button.textContent = language;
            button.classList.add("language-option");
            button.classList.toggle("active", language === activeLanguage);

            button.addEventListener("click", () => onLanguageChange(language));

            languageSelector.appendChild(button);
        });
    }

    // Rendert alle Kategorien und deren Level-Nodes.
    function renderLevels(levels, isCompleted) {
        skilltreeElement.innerHTML = "";

        const svg = createConnectionLayer();
        skilltreeElement.appendChild(svg);

        const categories = groupByCategory(levels);

        categories.forEach((category) => {
            const categoryBranch = createCategoryBranch(category, isCompleted);
            skilltreeElement.appendChild(categoryBranch);
        });

        // Startet Verbindungen und Physics erst, nachdem alle Nodes im DOM liegen.
        requestAnimationFrame(() => {
            drawConnections();

            initializeSkilltreePhysics({
                skilltreeElement,
                onUpdate: drawConnections
            });
        });

        // Registriert die Neuberechnung der Verbindungen bei Größenänderungen einmalig.
        if (!resizeHandlerRegistered) {
            window.addEventListener("resize", () =>
                requestAnimationFrame(drawConnections)
            );

            resizeHandlerRegistered = true;
        }
    }

    // Erstellt einen transparenten SVG-Layer für alle Verbindungen.
    function createConnectionLayer() {
        const svg = document.createElementNS(
            "http://www.w3.org/2000/svg",
            "svg"
        );

        svg.classList.add("skilltree-connections");
        svg.setAttribute("aria-hidden", "true");

        return svg;
    }

    // Erstellt den Bereich einer einzelnen Kategorie mit den zugehörigen Level-Nodes.
    function createCategoryBranch(category, isCompleted) {
        const branch = document.createElement("section");

        branch.classList.add("skilltree-branch");
        branch.dataset.categoryId = category.id;

        const categoryNode = createCategoryNode(category);
        branch.appendChild(categoryNode);

        const levelContainer = document.createElement("div");
        levelContainer.classList.add("skilltree-levels");

        category.levels.forEach((level, index) => {
            const levelNode = createLevelNode(
                level,
                isCompleted(level.levelID)
            );

            positionLevelNode(levelNode, index, category.levels.length);
            levelContainer.appendChild(levelNode);
        });

        branch.appendChild(levelContainer);

        return branch;
    }

    // Erstellt den zentralen Node einer Kategorie.
    function createCategoryNode(category) {
        const node = document.createElement("div");

        node.classList.add("skilltree-category-node");
        node.dataset.categoryId = category.id;
        node.dataset.categoryOrder = category.order;

        const name = document.createElement("span");

        name.classList.add("skilltree-category-name");
        name.textContent = category.name;

        node.appendChild(name);

        return node;
    }

    // Erstellt einen anklickbaren Level-Node und bindet dessen Interaktionen.
    function createLevelNode(level, completed) {
        const button = document.createElement("button");

        button.type = "button";
        button.classList.add("skilltree-level-node");

        if (completed) {
            button.classList.add("completed");
        }

        button.dataset.levelId = level.levelID;
        button.dataset.levelNumber = level.levelNumber;

        const circle = document.createElement("span");
        circle.classList.add("skilltree-level-circle");

        const number = document.createElement("span");

        number.classList.add("skilltree-level-number");
        number.textContent = level.levelNumber;

        const label = document.createElement("span");

        label.classList.add("skilltree-level-label");
        label.textContent = level.levelName;

        circle.appendChild(number);
        button.appendChild(circle);
        button.appendChild(label);

        button.addEventListener("click", () => onLevelSelect(level));

        let hoverAnimationFrame = null;

        // Aktualisiert die SVG-Verbindungen während der Hover-Bewegung des Nodes.
        function updateConnectionsWhileHovered() {
            drawConnections();

            hoverAnimationFrame = requestAnimationFrame(
                updateConnectionsWhileHovered
            );
        }

        button.addEventListener("mouseenter", () => {
            if (hoverAnimationFrame !== null) {
                return;
            }

            updateConnectionsWhileHovered();
        });

        button.addEventListener("mouseleave", () => {
            if (hoverAnimationFrame !== null) {
                cancelAnimationFrame(hoverAnimationFrame);
                hoverAnimationFrame = null;
            }

            requestAnimationFrame(drawConnections);
        });

        return button;
    }

    // Verteilt die Level eines Branches fächerförmig über CSS-Positionsvariablen.
    function positionLevelNode(node, index, total) {
        const center = (total - 1) / 2;
        const relativeIndex = index - center;

        const spacing = total <= 3 ? 185 : 165;
        const x = relativeIndex * spacing;
        const y = 65 + Math.abs(relativeIndex) * 38;

        node.style.setProperty("--node-x", `${x}px`);
        node.style.setProperty("--node-y", `${y}px`);
    }

    // Zeichnet sämtliche Verbindungen anhand der aktuellen DOM-Positionen neu.
    function drawConnections() {
        const svg = skilltreeElement.querySelector(".skilltree-connections");

        if (!svg) {
            return;
        }

        svg.innerHTML = "";

        const branches = [
            ...skilltreeElement.querySelectorAll(".skilltree-branch")
        ];

        branches.forEach((branch, index) => {
            const categoryNode = branch.querySelector(".skilltree-category-node");
            const levelNodes = branch.querySelectorAll(".skilltree-level-node");

            // Verbindet die Kategorie mit ihren einzelnen Leveln.
            levelNodes.forEach((levelNode) => {
                drawConnection(
                    svg,
                    categoryNode,
                    levelNode,
                    "skilltree-level-connection"
                );
            });

            const nextBranch = branches[index + 1];

            // Verbindet die Kategorie mit der nachfolgenden Kategorie.
            if (nextBranch) {
                const nextCategoryNode = nextBranch.querySelector(
                    ".skilltree-category-node"
                );

                drawConnection(
                    svg,
                    categoryNode,
                    nextCategoryNode,
                    "skilltree-category-connection"
                );
            }
        });
    }

    // Zeichnet eine einzelne SVG-Linie zwischen zwei DOM-Elementen.
    function drawConnection(svg, fromElement, toElement, cssClass) {
        if (!fromElement || !toElement) {
            return;
        }

        const treeRect = skilltreeElement.getBoundingClientRect();
        const fromRect = fromElement.getBoundingClientRect();
        const toRect = toElement.getBoundingClientRect();

        const x1 = fromRect.left + fromRect.width / 2 - treeRect.left;
        const y1 = fromRect.top + fromRect.height / 2 - treeRect.top;
        const x2 = toRect.left + toRect.width / 2 - treeRect.left;
        const y2 = toRect.top + toRect.height / 2 - treeRect.top;

        const line = document.createElementNS(
            "http://www.w3.org/2000/svg",
            "line"
        );

        line.setAttribute("x1", x1);
        line.setAttribute("y1", y1);
        line.setAttribute("x2", x2);
        line.setAttribute("y2", y2);
        line.classList.add(cssClass);

        svg.appendChild(line);
    }

    return {
        renderLanguageSelector,
        renderLevels
    };
}

// Gruppiert Level nach Kategorie und sortiert Kategorien sowie Level nach ihrer Reihenfolge.
function groupByCategory(levels) {
    const categoryMap = new Map();

    levels.forEach((level) => {
        if (!categoryMap.has(level.categoryID)) {
            categoryMap.set(level.categoryID, {
                id: level.categoryID,
                name: level.category,
                order: level.categoryOrder,
                levels: []
            });
        }

        categoryMap.get(level.categoryID).levels.push(level);
    });

    const categories = [...categoryMap.values()];

    categories.sort((a, b) => a.order - b.order);

    categories.forEach((category) => {
        category.levels.sort((a, b) => a.levelNumber - b.levelNumber);
    });

    return categories;
}