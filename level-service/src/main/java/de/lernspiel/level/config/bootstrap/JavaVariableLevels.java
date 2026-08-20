package de.lernspiel.level.config.bootstrap;

import java.util.List;

import org.springframework.stereotype.Component;

import de.lernspiel.common.code.CodeType;
import de.lernspiel.level.dto.CreateLevelRequest;
import de.lernspiel.level.dto.LevelComponentRequest;

/**
 * Enthält die fest definierten Java-Level der Kategorie VARIABLES.
 */
@Component
public class JavaVariableLevels
        implements LevelDefinitionProvider {


    /**
     * Liefert alle Java-VARIABLES-Level in der vorgesehenen Reihenfolge.
     */
    @Override
    public List<CreateLevelRequest> createLevels() {

        return List.of(
                createLevel1(),
                createLevel2(),
                createLevel3(),
                createLevel4()
        );
    }


    private CreateLevelRequest createLevel1() {

        return createLevel(
                "Variablen addieren",
                "Addiere zwei Variablen und speichere das Ergebnis in einer neuen Variable.",
                1,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 3),
                        component(CodeType.EQUALS, 1),
                        component(CodeType.ADD, 1),
                        component(CodeType.BREAK, 1)
                )
        );
    }


    private CreateLevelRequest createLevel2() {

        return createLevel(
                "Variablen multiplizieren",
                "Multipliziere zwei Variablen und speichere das Ergebnis in einer neuen Variable.",
                2,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 3),
                        component(CodeType.EQUALS, 1),
                        component(CodeType.MULTIPLY, 1),
                        component(CodeType.BREAK, 1)
                )
        );
    }


    private CreateLevelRequest createLevel3() {

        return createLevel(
                "Variablen subtrahieren",
                "Subtrahiere eine Variable von einer anderen und speichere das Ergebnis in einer neuen Variable.",
                3,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 3),
                        component(CodeType.EQUALS, 1),
                        component(CodeType.SUBTRACT, 1),
                        component(CodeType.BREAK, 1)
                )
        );
    }


    private CreateLevelRequest createLevel4() {

        return createLevel(
                "Variablen dividieren",
                "Dividiere eine Variable durch eine andere und speichere das Ergebnis in einer neuen Variable.",
                4,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 3),
                        component(CodeType.EQUALS, 1),
                        component(CodeType.DIVIDE, 1),
                        component(CodeType.BREAK, 1)
                )
        );
    }


    /**
     * Erstellt die gemeinsamen Metadaten eines Java-VARIABLES-Levels.
     */
    private CreateLevelRequest createLevel(
            String levelName,
            String levelDescription,
            Integer levelNumber,
            List<LevelComponentRequest> components) {

        CreateLevelRequest request =
                new CreateLevelRequest();

        request.setLevelName(
                levelName
        );

        request.setLevelDescription(
                levelDescription
        );

        request.setCategory(
                "VARIABLES"
        );

        request.setCategoryOrder(
                2
        );

        request.setLevelNumber(
                levelNumber
        );

        request.setLanguage(
                "JAVA"
        );

        request.setComponents(
                components
        );


        return request;
    }


    /**
     * Erstellt eine verfügbare Code-Komponente für ein Level.
     */
    private LevelComponentRequest component(
            CodeType type,
            Integer amount) {

        LevelComponentRequest component =
                new LevelComponentRequest();

        component.setType(
                type
        );

        component.setAmount(
                amount
        );


        return component;
    }
}