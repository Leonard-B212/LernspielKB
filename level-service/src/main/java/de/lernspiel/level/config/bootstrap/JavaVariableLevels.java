package de.lernspiel.level.config.bootstrap;

import java.util.List;

import org.springframework.stereotype.Component;

import de.lernspiel.common.code.CodeType;
import de.lernspiel.common.code.ExecutionLog;
import de.lernspiel.level.dto.CreateLevelRequest;
import de.lernspiel.level.dto.LevelComponentRequest;

import static de.lernspiel.level.config.bootstrap.ExpectedExecutionLogs.*;

/**
 * Enthält die fest definierten Java-Level der Kategorie VARIABLES.
 */
@Component
public class JavaVariableLevels implements LevelDefinitionProvider {

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
                "Variablen addieren",
                "Erstelle die int-Variable \"x\" mit dem Wert 5 und die int-Variable \"y\" mit dem Wert 3. Addiere anschließend \"x\" und \"y\" und speichere das Ergebnis in der int-Variable \"result\".",
                1,
                List.of(
                        component(CodeType.INT, 3),
                        component(CodeType.VAR_NAME, 5),
                        component(CodeType.EQUALS, 3),
                        component(CodeType.VALUE, 2),
                        component(CodeType.ADD, 1),
                        component(CodeType.BREAK, 3)
                ),
                log(
                        assignment(CodeType.INT, "x", 5),
                        assignment(CodeType.INT, "y", 3),
                        expressionAssignment(
                                CodeType.INT,
                                "result",
                                8,
                                "ARITHMETIC_EXPRESSION",
                                List.of(variable("x"), variable("y")),
                                List.of("ADD")
                        )
                )
        );
    }

    private static CreateLevelRequest createLevel2() {
        return createLevel(
                "Variablen multiplizieren",
                "Erstelle die int-Variable \"x\" mit dem Wert 4 und die int-Variable \"y\" mit dem Wert 3. Multipliziere anschließend \"x\" und \"y\" und speichere das Ergebnis in der int-Variable \"result\".",
                2,
                List.of(
                        component(CodeType.INT, 3),
                        component(CodeType.VAR_NAME, 5),
                        component(CodeType.EQUALS, 3),
                        component(CodeType.VALUE, 2),
                        component(CodeType.MULTIPLY, 1),
                        component(CodeType.BREAK, 3)
                ),
                log(
                        assignment(CodeType.INT, "x", 4),
                        assignment(CodeType.INT, "y", 3),
                        expressionAssignment(
                                CodeType.INT,
                                "result",
                                12,
                                "ARITHMETIC_EXPRESSION",
                                List.of(variable("x"), variable("y")),
                                List.of("MULTIPLY")
                        )
                )
        );
    }

    private static CreateLevelRequest createLevel3() {
        return createLevel(
                "Variablen subtrahieren",
                "Erstelle die int-Variable \"x\" mit dem Wert 10 und die int-Variable \"y\" mit dem Wert 4. Subtrahiere anschließend \"y\" von \"x\" und speichere das Ergebnis in der int-Variable \"result\".",
                3,
                List.of(
                        component(CodeType.INT, 3),
                        component(CodeType.VAR_NAME, 5),
                        component(CodeType.EQUALS, 3),
                        component(CodeType.VALUE, 2),
                        component(CodeType.SUBTRACT, 1),
                        component(CodeType.BREAK, 3)
                ),
                log(
                        assignment(CodeType.INT, "x", 10),
                        assignment(CodeType.INT, "y", 4),
                        expressionAssignment(
                                CodeType.INT,
                                "result",
                                6,
                                "ARITHMETIC_EXPRESSION",
                                List.of(variable("x"), variable("y")),
                                List.of("SUBTRACT")
                        )
                )
        );
    }

    private static CreateLevelRequest createLevel4() {
        return createLevel(
                "Variablen dividieren",
                "Erstelle die int-Variable \"x\" mit dem Wert 20 und die int-Variable \"y\" mit dem Wert 4. Dividiere anschließend \"x\" durch \"y\" und speichere das Ergebnis in der int-Variable \"result\".",
                4,
                List.of(
                        component(CodeType.INT, 3),
                        component(CodeType.VAR_NAME, 5),
                        component(CodeType.EQUALS, 3),
                        component(CodeType.VALUE, 2),
                        component(CodeType.DIVIDE, 1),
                        component(CodeType.BREAK, 3)
                ),
                log(
                        assignment(CodeType.INT, "x", 20),
                        assignment(CodeType.INT, "y", 4),
                        expressionAssignment(
                                CodeType.INT,
                                "result",
                                5,
                                "ARITHMETIC_EXPRESSION",
                                List.of(variable("x"), variable("y")),
                                List.of("DIVIDE")
                        )
                )
        );
    }

    private static CreateLevelRequest createLevel(String levelName, String levelDescription,
            Integer levelNumber, List<LevelComponentRequest> components, ExecutionLog expectedExecutionLog) {

        CreateLevelRequest request = new CreateLevelRequest();

        request.setLevelName(levelName);
        request.setLevelDescription(levelDescription);
        request.setCategory("VARIABLES");
        request.setCategoryOrder(2);
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