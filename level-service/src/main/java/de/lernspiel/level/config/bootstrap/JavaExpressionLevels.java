package de.lernspiel.level.config.bootstrap;

import java.util.List;

import org.springframework.stereotype.Component;

import de.lernspiel.common.code.CodeType;
import de.lernspiel.level.dto.CreateLevelRequest;
import de.lernspiel.level.dto.LevelComponentRequest;

/**
 * Enthält die fest definierten Java-Level der Kategorie EXPRESSIONS.
 */
@Component
public class JavaExpressionLevels implements LevelDefinitionProvider {

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
                "Einfache Addition",
                "Erstelle eine int-Variable result und speichere das Ergebnis von 5 + 3 darin.",
                1,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 1),
                        component(CodeType.EQUALS, 1),
                        component(CodeType.VALUE, 2),
                        component(CodeType.ADD, 1),
                        component(CodeType.BREAK, 1)
                )
        );
    }

    private static CreateLevelRequest createLevel2() {
        return createLevel(
                "Subtraktion",
                "Erstelle eine int-Variable result und speichere das Ergebnis von 10 - 4 darin.",
                2,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 1),
                        component(CodeType.EQUALS, 1),
                        component(CodeType.VALUE, 2),
                        component(CodeType.SUBTRACT, 1),
                        component(CodeType.BREAK, 1)
                )
        );
    }

    private static CreateLevelRequest createLevel3() {
        return createLevel(
                "Multiplikation",
                "Erstelle eine int-Variable result und speichere das Ergebnis von 6 * 3 darin.",
                3,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 1),
                        component(CodeType.EQUALS, 1),
                        component(CodeType.VALUE, 2),
                        component(CodeType.MULTIPLY, 1),
                        component(CodeType.BREAK, 1)
                )
        );
    }

    private static CreateLevelRequest createLevel4() {
        return createLevel(
                "Division",
                "Erstelle eine int-Variable result und speichere das Ergebnis von 20 / 4 darin.",
                4,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 1),
                        component(CodeType.EQUALS, 1),
                        component(CodeType.VALUE, 2),
                        component(CodeType.DIVIDE, 1),
                        component(CodeType.BREAK, 1)
                )
        );
    }

    private static CreateLevelRequest createLevel5() {
        return createLevel(
                "Mehrere Rechenoperationen",
                "Erstelle eine int-Variable result und speichere das Ergebnis von 5 + 3 * 2 darin.",
                5,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 1),
                        component(CodeType.EQUALS, 1),
                        component(CodeType.VALUE, 3),
                        component(CodeType.ADD, 1),
                        component(CodeType.MULTIPLY, 1),
                        component(CodeType.BREAK, 1)
                )
        );
    }

    private static CreateLevelRequest createLevel(String levelName, String levelDescription,
            Integer levelNumber, List<LevelComponentRequest> components) {

        CreateLevelRequest request = new CreateLevelRequest();

        request.setLevelName(levelName);
        request.setLevelDescription(levelDescription);
        request.setCategory("EXPRESSIONS");
        request.setCategoryOrder(3);
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