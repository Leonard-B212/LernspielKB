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
 * Enthält die fest definierten Java-Level der Kategorie LOOPS.
 */
@Component
public class JavaLoopLevels implements LevelDefinitionProvider {

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
                "Erste While-Schleife",
                "Erstelle die int-Variable \"x\" mit dem Wert 0. Erhöhe x solange um 1, wie x kleiner als 3 ist.",
                1,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 3),
                        component(CodeType.EQUALS, 2),
                        component(CodeType.VALUE, 3),
                        component(CodeType.SMALLER_THAN, 1),
                        component(CodeType.ADD, 1),
                        component(CodeType.WHILE_LOOP, 1),
                        component(CodeType.BREAK, 2)
                ),
                log(
                        assignment(CodeType.INT, "x", 0),
                        conditional(
                                LogType.WHILE_LOOP_ENTERED,
                                CodeType.SMALLER_THAN,
                                false,
                                variable("x"),
                                literal(3)
                        ),
                        expressionValueAssignment(
                                CodeType.INT,
                                "x",
                                1,
                                "ARITHMETIC_EXPRESSION",
                                List.of(variable("x"), literal(1)),
                                List.of("ADD")
                        ),
                        whileFinished(3)
                )
        );
    }

    private static CreateLevelRequest createLevel2() {
        return createLevel(
                "Anderer Startwert",
                "Erstelle die int-Variable \"x\" mit dem Wert 2. Erhöhe x solange um 1, wie x kleiner als 5 ist.",
                2,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 3),
                        component(CodeType.EQUALS, 2),
                        component(CodeType.VALUE, 3),
                        component(CodeType.SMALLER_THAN, 1),
                        component(CodeType.ADD, 1),
                        component(CodeType.WHILE_LOOP, 1),
                        component(CodeType.BREAK, 2)
                ),
                log(
                        assignment(CodeType.INT, "x", 2),
                        conditional(
                                LogType.WHILE_LOOP_ENTERED,
                                CodeType.SMALLER_THAN,
                                false,
                                variable("x"),
                                literal(5)
                        ),
                        expressionValueAssignment(
                                CodeType.INT,
                                "x",
                                3,
                                "ARITHMETIC_EXPRESSION",
                                List.of(variable("x"), literal(1)),
                                List.of("ADD")
                        ),
                        whileFinished(3)
                )
        );
    }

    private static CreateLevelRequest createLevel3() {
        return createLevel(
                "Größere Schritte",
                "Erstelle die int-Variable \"x\" mit dem Wert 2. Erhöhe x solange um 2, wie x kleiner als 8 ist.",
                3,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 3),
                        component(CodeType.EQUALS, 2),
                        component(CodeType.VALUE, 3),
                        component(CodeType.SMALLER_THAN, 1),
                        component(CodeType.ADD, 1),
                        component(CodeType.WHILE_LOOP, 1),
                        component(CodeType.BREAK, 2)
                ),
                log(
                        assignment(CodeType.INT, "x", 2),
                        conditional(
                                LogType.WHILE_LOOP_ENTERED,
                                CodeType.SMALLER_THAN,
                                false,
                                variable("x"),
                                literal(8)
                        ),
                        expressionValueAssignment(
                                CodeType.INT,
                                "x",
                                4,
                                "ARITHMETIC_EXPRESSION",
                                List.of(variable("x"), literal(2)),
                                List.of("ADD")
                        ),
                        whileFinished(3)
                )
        );
    }

    private static CreateLevelRequest createLevel4() {
        return createLevel(
                "Variable Grenze",
                "Erstelle die int-Variablen \"x\" mit dem Wert 0 und \"limit\" mit dem Wert 4. Erhöhe x solange um 1, wie x kleiner als limit ist.",
                4,
                List.of(
                        component(CodeType.INT, 2),
                        component(CodeType.VAR_NAME, 6),
                        component(CodeType.EQUALS, 3),
                        component(CodeType.VALUE, 3),
                        component(CodeType.SMALLER_THAN, 1),
                        component(CodeType.ADD, 1),
                        component(CodeType.WHILE_LOOP, 1),
                        component(CodeType.BREAK, 3)
                ),
                log(
                        assignment(CodeType.INT, "x", 0),
                        assignment(CodeType.INT, "limit", 4),
                        conditional(
                                LogType.WHILE_LOOP_ENTERED,
                                CodeType.SMALLER_THAN,
                                false,
                                variable("x"),
                                variable("limit")
                        ),
                        expressionValueAssignment(
                                CodeType.INT,
                                "x",
                                1,
                                "ARITHMETIC_EXPRESSION",
                                List.of(variable("x"), literal(1)),
                                List.of("ADD")
                        ),
                        whileFinished(4)
                )
        );
    }

    private static CreateLevelRequest createLevel5() {
        return createLevel(
                "Rückwärts zählen",
                "Erstelle die int-Variable \"x\" mit dem Wert 6. Verringere x solange um 2, wie x größer als 0 ist.",
                5,
                List.of(
                        component(CodeType.INT, 1),
                        component(CodeType.VAR_NAME, 3),
                        component(CodeType.EQUALS, 2),
                        component(CodeType.VALUE, 3),
                        component(CodeType.GREATER_THAN, 1),
                        component(CodeType.SUBTRACT, 1),
                        component(CodeType.WHILE_LOOP, 1),
                        component(CodeType.BREAK, 2)
                ),
                log(
                        assignment(CodeType.INT, "x", 6),
                        conditional(
                                LogType.WHILE_LOOP_ENTERED,
                                CodeType.GREATER_THAN,
                                false,
                                variable("x"),
                                literal(0)
                        ),
                        expressionValueAssignment(
                                CodeType.INT,
                                "x",
                                4,
                                "ARITHMETIC_EXPRESSION",
                                List.of(variable("x"), literal(2)),
                                List.of("SUBTRACT")
                        ),
                        whileFinished(3)
                )
        );
    }

    private static CreateLevelRequest createLevel(String levelName, String levelDescription,
            Integer levelNumber, List<LevelComponentRequest> components, ExecutionLog expectedExecutionLog) {

        CreateLevelRequest request = new CreateLevelRequest();

        request.setLevelName(levelName);
        request.setLevelDescription(levelDescription);
        request.setCategory("LOOPS");
        request.setCategoryOrder(6);
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