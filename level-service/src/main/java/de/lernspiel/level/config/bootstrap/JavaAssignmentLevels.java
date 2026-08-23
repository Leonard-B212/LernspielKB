package de.lernspiel.level.config.bootstrap;

import java.util.List;

import org.springframework.stereotype.Component;

import de.lernspiel.common.code.CodeType;
import de.lernspiel.level.dto.CreateLevelRequest;
import de.lernspiel.level.dto.LevelComponentRequest;

/**
 * Enthält die fest definierten Java-Level der Kategorie ASSIGNMENTS.
 */
@Component
public class JavaAssignmentLevels implements LevelDefinitionProvider {

    @Override
    public List<CreateLevelRequest> createLevels() {
        return List.of(
                createLevel1(),
                createLevel2(),
                createLevel3(),
                createLevel4()
        );
    }

    private static CreateLevelRequest createLevel1() {
        return createLevel(
                "Neuer Wert",
                "Erstelle eine int-Variable x mit dem Wert 5 und weise ihr anschließend den Wert 10 zu.",
                1,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 2),
                        component(CodeType.EQUALS, 2),
                        component(CodeType.VALUE, 2),
                        component(CodeType.BREAK, 2)
                )
        );
    }

    private static CreateLevelRequest createLevel2() {
        return createLevel(
                "Wert übernehmen",
                "Erstelle die int-Variablen x und y. Setze x auf 5 und weise anschließend den Wert von x der Variable y zu.",
                2,
                List.of(
                        component(CodeType.INT, 2),
                        component(CodeType.VAR_NAME, 3),
                        component(CodeType.EQUALS, 2),
                        component(CodeType.VALUE, 1),
                        component(CodeType.BREAK, 2)
                )
        );
    }

    private static CreateLevelRequest createLevel3() {
        return createLevel(
                "Variable erhöhen",
                "Erstelle eine int-Variable x mit dem Wert 5 und erhöhe ihren Wert anschließend um 2.",
                3,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 2),
                        component(CodeType.EQUALS, 2),
                        component(CodeType.VALUE, 2),
                        component(CodeType.ADD, 1),
                        component(CodeType.BREAK, 2)
                )
        );
    }

    private static CreateLevelRequest createLevel4() {
        return createLevel(
                "Mehrere Zuweisungen",
                "Erstelle eine int-Variable x mit dem Wert 5. Setze x anschließend auf 10 und danach auf 15.",
                4,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 3),
                        component(CodeType.EQUALS, 3),
                        component(CodeType.VALUE, 3),
                        component(CodeType.BREAK, 3)
                )
        );
    }

    private static CreateLevelRequest createLevel(String levelName, String levelDescription,
            Integer levelNumber, List<LevelComponentRequest> components) {

        CreateLevelRequest request = new CreateLevelRequest();

        request.setLevelName(levelName);
        request.setLevelDescription(levelDescription);
        request.setCategory("ASSIGNMENTS");
        request.setCategoryOrder(4);
        request.setLevelNumber(levelNumber);
        request.setLanguage("JAVA");
        request.setComponents(components);

        return request;
    }

    private static LevelComponentRequest component(CodeType type, Integer amount) {
        LevelComponentRequest component = new LevelComponentRequest();

        component.setType(type);
        component.setAmount(amount);

        return component;
    }
}