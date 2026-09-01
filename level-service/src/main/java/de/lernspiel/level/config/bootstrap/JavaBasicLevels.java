package de.lernspiel.level.config.bootstrap;

import java.util.List;

import org.springframework.stereotype.Component;

import de.lernspiel.common.code.CodeType;
import de.lernspiel.common.code.ExecutionLog;
import de.lernspiel.level.dto.CreateLevelRequest;
import de.lernspiel.level.dto.LevelComponentRequest;

import static de.lernspiel.level.config.bootstrap.ExpectedExecutionLogs.*;

/**
 * Enthält die fest definierten Java-Level der Kategorie BASICS.
 */
@Component
public class JavaBasicLevels implements LevelDefinitionProvider {

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
                ),
                log(
                        assignment(CodeType.INT, "x", 5)
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
                ),
                log(
                        assignment(CodeType.STRING, "name", "Hallo")
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
                ),
                log(
                        assignment(CodeType.BOOLEAN, "isActive", true)
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
                ),
                log(
                        declaration(CodeType.INT, "number")
                )
        );
    }

    private static CreateLevelRequest createLevel5() {
        return createLevel(
                "Zwei Variablen",
                "Erstelle die int-Variable x mit dem Wert 5 und die int-Variable y mit dem Wert 10.",
                5,
                List.of(
                        component(CodeType.INT, 2),
                        component(CodeType.VAR_NAME, 2),
                        component(CodeType.EQUALS, 2),
                        component(CodeType.VALUE, 2),
                        component(CodeType.BREAK, 2)
                ),
                log(
                        assignment(CodeType.INT, "x", 5),
                        assignment(CodeType.INT, "y", 10)
                )
        );
    }

    private static CreateLevelRequest createLevel(String levelName, String levelDescription,
            Integer levelNumber, List<LevelComponentRequest> components, ExecutionLog expectedExecutionLog) {

        CreateLevelRequest request = new CreateLevelRequest();

        request.setLevelName(levelName);
        request.setLevelDescription(levelDescription);
        request.setCategory("BASICS");
        request.setCategoryOrder(1);
        request.setLevelNumber(levelNumber);
        request.setLanguage("JAVA");
        request.setComponents(components);
        request.setExpectedExecutionLog(expectedExecutionLog);

        return request;
    }

    private static LevelComponentRequest component(CodeType type, Integer amount) {
        LevelComponentRequest component = new LevelComponentRequest();

        component.setType(type);
        component.setAmount(amount);

        return component;
    }
}