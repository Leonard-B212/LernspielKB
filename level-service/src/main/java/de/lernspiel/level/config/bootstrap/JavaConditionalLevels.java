package de.lernspiel.level.config.bootstrap;

import java.util.List;

import org.springframework.stereotype.Component;

import de.lernspiel.common.code.CodeType;
import de.lernspiel.common.code.ExecutionLog;
import de.lernspiel.common.code.LogType;
import de.lernspiel.level.dto.CreateLevelRequest;
import de.lernspiel.level.dto.LevelComponentRequest;

import static de.lernspiel.level.config.bootstrap.ExpectedExecutionLogs.*;

/**
 * Enthält die fest definierten Java-Level der Kategorie CONDITIONALS.
 */
@Component
public class JavaConditionalLevels implements LevelDefinitionProvider {

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
                "Einfaches If",
                "Erstelle die int-Variable \"x\" mit dem Wert 5. Wenn x kleiner als 10 ist, setze x auf 10.",
                1,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 2),
                        component(CodeType.EQUALS, 2),
                        component(CodeType.VALUE, 3),
                        component(CodeType.SMALLER_THAN, 1),
                        component(CodeType.IF_STATEMENT, 1),
                        component(CodeType.BREAK, 2)
                ),
                log(
                        assignment(CodeType.INT, "x", 5),
                        conditional(
                                LogType.IF_BRANCH_ENTERED,
                                CodeType.SMALLER_THAN,
                                false,
                                variable("x"),
                                literal(10)
                        ),
                        valueAssignment(CodeType.INT, "x", 10)
                )
        );
    }

    private static CreateLevelRequest createLevel2() {
        return createLevel(
                "Größer als",
                "Erstelle die int-Variable \"x\" mit dem Wert 15. Wenn x größer als 10 ist, setze x auf 20.",
                2,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 2),
                        component(CodeType.EQUALS, 2),
                        component(CodeType.VALUE, 3),
                        component(CodeType.GREATER_THAN, 1),
                        component(CodeType.IF_STATEMENT, 1),
                        component(CodeType.BREAK, 2)
                ),
                log(
                        assignment(CodeType.INT, "x", 15),
                        conditional(
                                LogType.IF_BRANCH_ENTERED,
                                CodeType.GREATER_THAN,
                                false,
                                variable("x"),
                                literal(10)
                        ),
                        valueAssignment(CodeType.INT, "x", 20)
                )
        );
    }

    private static CreateLevelRequest createLevel3() {
        return createLevel(
                "If und Else",
                "Erstelle die int-Variable \"x\" mit dem Wert 5. Wenn x größer als 10 ist, setze x auf 20. Andernfalls setze x auf 10.",
                3,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 3),
                        component(CodeType.EQUALS, 3),
                        component(CodeType.VALUE, 4),
                        component(CodeType.GREATER_THAN, 1),
                        component(CodeType.IF_STATEMENT, 1),
                        component(CodeType.ELSE_STATEMENT, 1),
                        component(CodeType.BREAK, 3)
                ),
                log(
                        assignment(CodeType.INT, "x", 5),
                        conditional(
                                LogType.IF_BRANCH_PREVIEWED,
                                CodeType.GREATER_THAN,
                                false,
                                variable("x"),
                                literal(10)
                        ),
                        new de.lernspiel.common.code.LogFile(
                                new java.util.HashMap<>(),
                                LogType.ELSE_BRANCH_ENTERED
                        ),
                        valueAssignment(CodeType.INT, "x", 10)
                )
        );
    }

    private static CreateLevelRequest createLevel4() {
        return createLevel(
                "Variablen vergleichen",
                "Erstelle die int-Variablen \"x\" mit dem Wert 5 und \"limit\" mit dem Wert 10. Wenn x kleiner als limit ist, setze x auf den Wert von limit.",
                4,
                List.of(
                        component(CodeType.INT, 2),
                        component(CodeType.VAR_NAME, 5),
                        component(CodeType.EQUALS, 3),
                        component(CodeType.VALUE, 2),
                        component(CodeType.SMALLER_THAN, 1),
                        component(CodeType.IF_STATEMENT, 1),
                        component(CodeType.BREAK, 3)
                ),
                log(
                        assignment(CodeType.INT, "x", 5),
                        assignment(CodeType.INT, "limit", 10),
                        conditional(
                                LogType.IF_BRANCH_ENTERED,
                                CodeType.SMALLER_THAN,
                                false,
                                variable("x"),
                                variable("limit")
                        ),
                        expressionValueAssignment(
                                CodeType.INT,
                                "x",
                                10,
                                "VARIABLE_REFERENCE",
                                List.of(variable("limit")),
                                List.of()
                        )
                )
        );
    }

    private static CreateLevelRequest createLevel5() {
        return createLevel(
                "Else If",
                "Erstelle die int-Variable \"x\" mit dem Wert 10. Wenn x kleiner als 10 ist, setze x auf 1. Sonst, wenn x größer als 10 ist, setze x auf 2. Andernfalls setze x auf 3.",
                5,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 4),
                        component(CodeType.EQUALS, 4),
                        component(CodeType.VALUE, 6),
                        component(CodeType.SMALLER_THAN, 1),
                        component(CodeType.GREATER_THAN, 1),
                        component(CodeType.IF_STATEMENT, 2),
                        component(CodeType.ELSE_STATEMENT, 2),
                        component(CodeType.BREAK, 4)
                ),
                log(
                        assignment(CodeType.INT, "x", 10),
                        conditional(
                                LogType.IF_BRANCH_PREVIEWED,
                                CodeType.SMALLER_THAN,
                                false,
                                variable("x"),
                                literal(10)
                        ),
                        conditional(
                                LogType.ELSE_IF_BRANCH_PREVIEWED,
                                CodeType.GREATER_THAN,
                                false,
                                variable("x"),
                                literal(10)
                        ),
                        new de.lernspiel.common.code.LogFile(
                                new java.util.HashMap<>(),
                                LogType.ELSE_BRANCH_ENTERED
                        ),
                        valueAssignment(CodeType.INT, "x", 3)
                )
        );
    }

    private static CreateLevelRequest createLevel(String levelName, String levelDescription,
            Integer levelNumber, List<LevelComponentRequest> components, ExecutionLog expectedExecutionLog) {

        CreateLevelRequest request = new CreateLevelRequest();

        request.setLevelName(levelName);
        request.setLevelDescription(levelDescription);
        request.setCategory("CONDITIONALS");
        request.setCategoryOrder(5);
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