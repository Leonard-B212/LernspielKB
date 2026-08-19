/**
 * Rendert die Level des Skilltrees.
 *
 * Die visuelle Baumstruktur wird später ergänzt.
 * Aktuell werden die Level zunächst nach Kategorie gruppiert dargestellt.
 */
export function createSkilltreeRenderer({
    skilltreeElement,
    languageSelector,
    onLanguageChange,
    onLevelSelect
}) {


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
            button.textContent = language;

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


    function renderLevels(
        levels,
        isCompleted) {

        skilltreeElement.innerHTML = "";


        const categories =
            groupByCategory(levels);


        categories.forEach(category => {

            const section =
                document.createElement(
                    "section"
                );

            section.classList.add(
                "skilltree-category"
            );


            const heading =
                document.createElement(
                    "h2"
                );

            heading.textContent =
                category.name;


            section.appendChild(
                heading
            );


            category.levels.forEach(
                level => {

                    const button =
                        document.createElement(
                            "button"
                        );

                    button.type = "button";

                    button.textContent =
                        `${level.levelNumber}. ${level.levelName}`;

                    button.classList.add(
                        "skilltree-level"
                    );


                    if (
                        isCompleted(
                            level.levelID
                        )
                    ) {
                        button.classList.add(
                            "completed"
                        );
                    }


                    button.addEventListener(
                        "click",
                        () =>
                            onLevelSelect(
                                level
                            )
                    );


                    section.appendChild(
                        button
                    );
                }
            );


            skilltreeElement.appendChild(
                section
            );
        });
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
                    id: level.categoryID,
                    name: level.category,
                    order: level.categoryOrder,
                    levels: []
                }
            );
        }


        categoryMap
            .get(level.categoryID)
            .levels
            .push(level);
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