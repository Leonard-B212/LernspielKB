package de.lernspiel.level.config.bootstrap;

import java.util.List;

import org.springframework.stereotype.Component;

import de.lernspiel.common.code.CodeType;
import de.lernspiel.level.dto.CreateLevelRequest;
import de.lernspiel.level.dto.LevelComponentRequest;
import org.springframework.stereotype.Component;

/**
 * Enthält die fest definierten Java-Level der Kategorie BASICS.
 */
@Component
public class JavaBasicLevels
        implements LevelDefinitionProvider {

    


    /**
     * Liefert alle Java-BASICS-Level in der vorgesehenen Reihenfolge.
     */
    @Override
    public List<CreateLevelRequest> createLevels() {

        return List.of(
                createLevel1(),
                createLevel2(),
                createLevel3(),
                createLevel4(),
                createLevel5()
        );
    }


    private static CreateLevelRequest createLevel1() {

        return createLevel(
                "Erste Variable",
                "Erstelle eine int-Variable x mit dem Wert 5.",
                1,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 1),
                        component(CodeType.EQUALS, 1),
                        component(CodeType.VALUE, 1),
                        component(CodeType.BREAK, 1)
                )
        );
    }


    private static CreateLevelRequest createLevel2() {

        return createLevel(
                "String-Variable",
                "Erstelle eine String-Variable name mit dem Wert \"Hallo\".",
                2,
                List.of(
                        component(CodeType.STRING, 1),
                        component(CodeType.VAR_NAME, 1),
                        component(CodeType.EQUALS, 1),
                        component(CodeType.VALUE, 1),
                        component(CodeType.BREAK, 1)
                )
        );
    }


    private static CreateLevelRequest createLevel3() {

        return createLevel(
                "Boolean-Variable",
                "Erstelle eine boolean-Variable isActive mit dem Wert true.",
                3,
                List.of(
                        component(CodeType.BOOLEAN, 1),
                        component(CodeType.VAR_NAME, 1),
                        component(CodeType.EQUALS, 1),
                        component(CodeType.VALUE, 1),
                        component(CodeType.BREAK, 1)
                )
        );
    }


    private static CreateLevelRequest createLevel4() {

        return createLevel(
                "Variable ohne Startwert",
                "Deklariere eine int-Variable number ohne ihr direkt einen Wert zuzuweisen.",
                4,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 1),
                        component(CodeType.BREAK, 1)
                )
        );
    }


    private static CreateLevelRequest createLevel5() {

        return createLevel(
                "Zwei Variablen",
                "Erstelle zwei int-Variablen mit jeweils einem Wert.",
                5,
                List.of(
                        component(CodeType.INT, 2),
                        component(CodeType.VAR_NAME, 2),
                        component(CodeType.EQUALS, 2),
                        component(CodeType.VALUE, 2),
                        component(CodeType.BREAK, 2)
                )
        );
    }


    /**
     * Erstellt die gemeinsamen Metadaten eines Java-BASICS-Levels.
     */
    private static CreateLevelRequest createLevel(
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
                "BASICS"
        );

        request.setCategoryOrder(
                1
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
    private static LevelComponentRequest component(
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