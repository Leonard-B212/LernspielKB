package de.lernspiel.game.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import de.lernspiel.game.dto.CodeBlock;
import de.lernspiel.game.dto.ElseStatementBlock;
import de.lernspiel.game.dto.IfStatementBlock;
import de.lernspiel.game.dto.ProgramRequest;
import de.lernspiel.game.dto.ValueBlock;
import de.lernspiel.game.dto.VarNameBlock;
import de.lernspiel.game.dto.Variable;

import de.lernspiel.common.code.CodeType;
import de.lernspiel.common.code.ExecutionLog;
import de.lernspiel.common.code.LogFile;
import de.lernspiel.common.code.LogType;


@Service
public class InterpreterService {
    private static final Set<CodeType> ARITHMETIC_OPERATORS = EnumSet.of(CodeType.ADD, CodeType.SUBTRACT, CodeType.MULTIPLY, CodeType.DIVIDE);
    private static final Set<CodeType> STRING_CONCAT_OPERATORS = EnumSet.of(CodeType.ADD);

    public ExecutionLog run(ProgramRequest programRequest) {
        ExecutionLog output = new ExecutionLog();
        try {
            switch(programRequest.getLanguageId()){
                case 1: //Id für Java
                    interpreterMainJava(programRequest, output);
                    break;
                case 2: //Id für Python
                    //TODO: Python Interpreter
                    throw new UnsupportedOperationException("Python interpreter not implemented yet");
                default: //Id nicht implementiert
                    throw new IllegalArgumentException("Unknown language id: " + programRequest.getLanguageId());
            }

        } catch (Exception e) {
            Map<String, Object> logContents = new HashMap<>();
            logContents.put("errorType", e.getClass());
            logContents.put("errorMessage", e.getMessage());
            output.add(new LogFile(logContents, LogType.ERROR));
        }
        return output;
    }

    public void interpreterMainJava(ProgramRequest programRequest, ExecutionLog output){
        Map<String, Variable<?>> variables = new HashMap<>();
        List<String> globalVariables = new ArrayList<>();

        output.add(new LogFile(new HashMap<>(), LogType.PROGRAM_START));

        executeProgram(programRequest.getProgram(), variables, globalVariables, output);

        Map<String, Object> logContents = new HashMap<>();
        logContents.put("finalVariables", new HashMap<>(variables));
        output.add(new LogFile(logContents, LogType.PROGRAM_END));
    }

    public void executeProgram(List<CodeBlock> program, Map<String, Variable<?>> variables, List<String> localVariables, ExecutionLog output){
        List<CodeBlock[]> parsedCode = parseCode(program, output);
        for(CodeBlock[] lineOfCode : parsedCode){
            executeCode(lineOfCode, variables, localVariables, output);
        }
    }

    public void executeCode(CodeBlock[] lineOfCode, Map<String, Variable<?>> variables, List<String> localVariables, ExecutionLog output){
        CodeBlock firstBlock = lineOfCode[0];
        switch (firstBlock.getType()) {
            case STRING, INT, BOOLEAN:
                variableDeclaration(lineOfCode, variables, localVariables, output);
                break;
            case VAR_NAME:
                variableValueAssignment(lineOfCode, variables, output);
                break;
            case IF_STATEMENT:
                conditionalStatement(lineOfCode, variables, output);
                break;
            default:
                throw new IllegalArgumentException("Unexpected Start of Line: " + firstBlock.getType());
        }
    }

    public List<CodeBlock[]> parseCode(List<CodeBlock> program, ExecutionLog output) {
        List<CodeBlock[]> result = new ArrayList<>();
        List<CodeBlock> current = new ArrayList<>();

        for (CodeBlock block : program) {
            current.add(block);
            if (block.getType().equals(CodeType.BREAK)) {
                result.add(current.toArray(new CodeBlock[0]));
                current = new ArrayList<>();
            }
        }

        if (!current.isEmpty()) {
            throw new IllegalArgumentException("Expecting ';' on end of line, instead got: " + current.getLast().getType());
        }

        return result;
    }

    /**
     * Verarbeitet eine Variablendeklaration, z. B.:
     *   int x;          -> Deklaration ohne Initialwert
     *   int x = 5;       -> Deklaration mit Initialwert
     */
    public void variableDeclaration(CodeBlock[] lineOfCode, Map<String, Variable<?>> variables, List<String> localVariables, ExecutionLog output) {
        CodeType variableType = lineOfCode[0].getType();

        CodeBlock variableNameBlockRaw = requireBlock(lineOfCode, 1, "Erwarte einen Variablennamen an Position 1", output);

        if (!variableNameBlockRaw.getType().equals(CodeType.VAR_NAME)) {
            throw new IllegalArgumentException("Erwarte VAR_NAME an Position 1, war aber: " + variableNameBlockRaw.getType());
        }

        VarNameBlock variableNameBlock = (VarNameBlock) variableNameBlockRaw;
        String varName = variableNameBlock.getName();

        if(variableAlreadyDeclared(varName, variables)){
            throw new IllegalArgumentException("Variable bereits deklariert: " + varName);
        }

        CodeBlock thirdBlock = requireBlock(lineOfCode, 2, "Erwarte '=' oder ';' nach dem Variablennamen", output);

        if (thirdBlock.getType().equals(CodeType.BREAK)) {
            // Fall 1: Deklaration ohne Wertzuweisung, z. B. "int x;"
            Variable<?> emptyVariable = createEmptyVariable(variableType, output);
            variables.put(varName, emptyVariable);
            localVariables.add(varName);

            Map<String, Object> logContents = new HashMap<>();
            logContents.put("variableType", variableType);
            logContents.put("variableName", varName);
            output.add(new LogFile(logContents, LogType.SIMPLE_VARIABLE_DECLARATION));

            return;
        }

        if (!thirdBlock.getType().equals(CodeType.EQUALS)) {
            throw new IllegalArgumentException("Erwarte '=' oder ';' nach dem Variablennamen, war aber: " + thirdBlock.getType());
        }

        
        //Fall 2: Deklaration mit Wertzuweisung
        List<CodeBlock> valueAssignBlocks = new ArrayList<>();
        for(int i = 3; i < lineOfCode.length - 1; i++){
            valueAssignBlocks.add(requireBlock(lineOfCode, i, "Erwarte eine Wertzuweisung nach '='", output));
        }

        if(valueAssignBlocks.isEmpty()){
            throw new IllegalArgumentException("Erwarte eine Wertzuweisung nach '='");
        }

        Variable<?> initializedVariable = determineVariableValue(valueAssignBlocks, variables, variableType, output);

        CodeBlock terminator = requireBlock(lineOfCode, lineOfCode.length - 1, "Erwarte ';' am Ende der Deklaration", output);
        if (!terminator.getType().equals(CodeType.BREAK)) {
            throw new IllegalArgumentException("Erwarte ';' am Ende der Deklaration, war aber: " + terminator.getType());
        }

        variables.put(varName, initializedVariable);
        localVariables.add(varName);

        Map<String, Object> logContents = new HashMap<>();
        logContents.put("variableType", variableType);
        logContents.put("variableName", varName);
        logContents.put("variableValue", initializedVariable.getValue());
        logContents.putAll(describeValueAssignment(valueAssignBlocks, variableType));
        output.add(new LogFile(logContents, LogType.VARIABLE_DECLARATION_ASSIGNMENT));
    }

    public void variableValueAssignment(CodeBlock[] lineOfCode, Map<String, Variable<?>> variables, ExecutionLog output){
        VarNameBlock variableNameBlock = (VarNameBlock) lineOfCode[0];
        String varName = variableNameBlock.getName();

        if(!variableAlreadyDeclared(varName, variables)){
            throw new IllegalArgumentException("Variable wurde nicht deklariert: " + varName);
        }

        CodeBlock equalsBlock = requireBlock(lineOfCode, 1, "Erwarte '=' nach dem Variablennamen", output);
        if(!equalsBlock.getType().equals(CodeType.EQUALS)){
            throw new IllegalArgumentException("Erwarte '=' nach dem Variablennamen, war aber: " + equalsBlock.getType());
        }

        List<CodeBlock> valueAssignBlocks = new ArrayList<>();
        for(int i = 2; i < lineOfCode.length - 1; i++){
            valueAssignBlocks.add(requireBlock(lineOfCode, i, "Erwarte eine Wertzuweisung nach '='", output));
        }

        if(valueAssignBlocks.isEmpty()){
            throw new IllegalArgumentException("Erwarte eine Wertzuweisung nach '='");
        }

        Variable<?> newVariableValue = determineVariableValue(valueAssignBlocks, variables, variables.get(varName).getType(), output);

        CodeBlock terminator = requireBlock(lineOfCode, lineOfCode.length - 1, "Erwarte ';' am Ende der Deklaration", output);
        if (!terminator.getType().equals(CodeType.BREAK)) {
            throw new IllegalArgumentException("Erwarte ';' am Ende der Deklaration, war aber: " + terminator.getType());
        }

        variables.put(varName, newVariableValue);

        Map<String, Object> logContents = new HashMap<>();
        logContents.put("variableType", variables.get(varName).getType());
        logContents.put("variableName", varName);
        logContents.put("variableValue", newVariableValue.getValue());
        logContents.putAll(describeValueAssignment(valueAssignBlocks, variables.get(varName).getType()));
        output.add(new LogFile(logContents, LogType.VARIABLE_VALUE_ASSIGNMENT));
    }

    public void conditionalStatement(CodeBlock[] lineOfCode, Map<String, Variable<?>> variables, ExecutionLog output){
        IfStatementBlock firstIfBlock = (IfStatementBlock) lineOfCode[0];

        if(checkExpression(firstIfBlock, variables, output)){
            executeConditionalProgram(firstIfBlock.getProgram(), variables, output);
            return;
        }

        int position = 1;
        while(position < lineOfCode.length - 1){
            CodeBlock currentBlock = requireBlock(lineOfCode, position, "Conditional Statement", output);
            if(!currentBlock.getType().equals(CodeType.ELSE_STATEMENT)){
                throw new IllegalArgumentException("Erwarte Else-Statement, war aber : " + currentBlock.getType());
            }
            CodeBlock nextBlock = requireBlock(lineOfCode, position + 1, "Conditional Statement", output);
            if(nextBlock.getType().equals(CodeType.IF_STATEMENT)){
                IfStatementBlock ifBlock = (IfStatementBlock) nextBlock;
                if(checkExpression(ifBlock, variables, output)){
                    executeConditionalProgram(ifBlock.getProgram(), variables, output);
                    return;
                }
                position +=2;
            } else if(!nextBlock.getType().equals(CodeType.BREAK)){
                throw new IllegalArgumentException("Erwarte Else-If-Statement oder Else Statement, war aber : " + nextBlock.getType());
            } else {
                ElseStatementBlock elseBlock = (ElseStatementBlock) currentBlock;
                executeConditionalProgram(elseBlock.getProgram(), variables, output);
                return;
            }
        }
        CodeBlock terminator = requireBlock(lineOfCode, lineOfCode.length-1, "Erwarte ein Break am Ende eines Code-Abschnitts", output);
        if(!terminator.getType().equals(CodeType.BREAK)){
            throw new IllegalArgumentException("Erwarte Break, war aber : " + terminator.getType());
        }
    }

    public boolean checkExpression(IfStatementBlock ifBlock, Map<String, Variable<?>> variables, ExecutionLog output) {
        List<CodeBlock> expression = ifBlock.getExpression();
        if(expression.size() == 1){
            CodeBlock cb = expression.getFirst();
            if(cb.getType().equals(CodeType.VAR_NAME)){
                VarNameBlock varNameBlock = (VarNameBlock) cb;
                if(!variableAlreadyDeclared(varNameBlock.getName(), variables)){
                    throw new IllegalArgumentException("Variable " + varNameBlock.getName() + " wurde nicht deklariert");
                } else if(!variables.get(varNameBlock.getName()).getType().equals(CodeType.BOOLEAN)){
                    throw new IllegalArgumentException("Variable " + varNameBlock.getName() + " ist kein Boolean");
                }
                return (boolean) variables.get(varNameBlock.getName()).getValue();
            } else if(cb.getType().equals(CodeType.VALUE)){
                ValueBlock valueBlock = (ValueBlock) cb;
                Variable var = valueBlock.getValue();
                if(!var.getType().equals(CodeType.BOOLEAN)){
                    throw new IllegalArgumentException("Wert " + var.getValue() + " ist kein Boolean");
                }
                return (boolean) var.getValue();
            }
        }   
        // TODO Auto-generated method stub
        return true;
    }


    public void executeConditionalProgram(List<CodeBlock> program, Map<String, Variable<?>> variables, ExecutionLog output){
        List<String> localVariables = new ArrayList<>();
        try {
            executeProgram(program, variables, localVariables, output);
        } finally {
            for(String varName : localVariables){
                variables.remove(varName);
            }
        }
    }

    /**
     * Resolves the value(s) between '=' and ';' into a single Variable, according to the four supported assignment forms:
     *   1. a single ValueBlock
     *   2. a single VarNameBlock referencing an already-declared variable of the same type
     *   3. (STRING only) operand ADD operand ADD operand ...
     *   4. (INT only)    operand OP operand OP operand ...  where OP in {ADD, SUBTRACT, MULTIPLY, DIVIDE}
     */
    public Variable<?> determineVariableValue(List<CodeBlock> valueAssignBlocks, Map<String, Variable<?>> variables, CodeType type, ExecutionLog output) {
        if (valueAssignBlocks.size() == 1) {
            // Form 1 or 2: a single value or a single variable reference
            return resolveSingleOperand(valueAssignBlocks.get(0), type, variables, output);
        }

        // More than one block only makes sense for STRING (concatenation) or INT (arithmetic)
        switch (type) {
            case STRING:
                return evaluateStringConcatenation(valueAssignBlocks, variables, output);
            case INT:
                return evaluateIntegerExpression(valueAssignBlocks, variables, output);
            case BOOLEAN:
                throw new IllegalArgumentException("Boolean variables only support a single value or variable reference");
            default:
                throw new IllegalArgumentException("Unknown variable type: " + type);
        }
    }

    /** Resolves a single operand (ValueBlock or VarNameBlock) into a Variable of the expected type. */
    public Variable<?> resolveSingleOperand(CodeBlock block, CodeType expectedType, Map<String, Variable<?>> variables, ExecutionLog output) {
        if (block instanceof ValueBlock valueBlock) {
            return requireMatchingType(valueBlock.getValue(), expectedType, output);
        }
        if (block instanceof VarNameBlock varNameBlock) {
            return resolveVariableReference(varNameBlock.getName(), expectedType, variables, output);
        }
        throw new IllegalArgumentException("Expected a value or variable name, got: " + block.getType());
    }

    /**
     * Looks up a variable by name and validates it's actually usable:
     * - must be declared
     * - must actually have a value (catches "int x; y = x;" - using a declared-but-uninitialized variable, which would otherwise NPE silently during concatenation/arithmetic below)
     * - must match the expected type
     */
    public Variable<?> resolveVariableReference(String name, CodeType expectedType, Map<String, Variable<?>> variables, ExecutionLog output) {
        if (!variableAlreadyDeclared(name, variables)) {
            throw new IllegalArgumentException("Use of undeclared variable: " + name);
        }
        Variable<?> referenced = variables.get(name);
        if (referenced.getValue() == null) {
            throw new IllegalArgumentException("Use of declared but uninitialized variable: " + name);
        }
        return requireMatchingType(referenced, expectedType, output);
    }

    public Class<?> javaClassFor(CodeType type, ExecutionLog output) {
        switch (type) {
            case STRING:  
                return String.class;
            case INT:
                return Integer.class;
            case BOOLEAN:
                return Boolean.class;
            default:
                throw new IllegalArgumentException("No Java type mapped for: " + type);
        }
    }

    @SuppressWarnings("unchecked") // safe: javaClassFor(expectedType) is guaranteed to return the Class matching T at every call site, since CodeType and the Java type are 1:1
    public <T> Variable<T> requireMatchingType(Variable<?> value, CodeType expectedType, ExecutionLog output) {
        if (value.getType() != expectedType) {
            throw new IllegalArgumentException("Type mismatch: expected " + expectedType + ", but value was of type " + value.getType());
        }
        Class<T> expectedClass = (Class<T>) javaClassFor(expectedType, output);
        T castValue = expectedClass.cast(value.getValue());
        return new Variable<>(castValue, expectedType);
    }

    /** Validates that blocks strictly alternate operand-operator-operand-...-operand. Catches malformed chains like "1 ADD ADD 2" or "1 ADD" (trailing operator) */
    public void validateAlternatingPattern(List<CodeBlock> blocks, Set<CodeType> allowedOperators, String context, ExecutionLog output) {
        if (blocks.size() % 2 == 0) {
            throw new IllegalArgumentException(context + ": expected an odd number of blocks (operand-operator-operand-...)");
        }
        for (int i = 0; i < blocks.size(); i++) {
            CodeType actual = blocks.get(i).getType();
            boolean operatorPosition = (i % 2 == 1);
            if (operatorPosition && !allowedOperators.contains(actual)) {
                throw new IllegalArgumentException(context + ": invalid operator at position " + i + ": " + actual + " (allowed: " + allowedOperators + ")");
            }
            if (!operatorPosition && !(actual.equals(CodeType.VALUE) || actual.equals(CodeType.VAR_NAME))) {
                throw new IllegalArgumentException(context + ": expected an operand at position " + i + ", but found: " + actual);
            }
        }
    }

    /** Form 3: STRING concatenation via ADD-blocks only. */
    public Variable<String> evaluateStringConcatenation(List<CodeBlock> blocks, Map<String, Variable<?>> variables, ExecutionLog output) {
        validateAlternatingPattern(blocks, STRING_CONCAT_OPERATORS, "String concatenation", output);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < blocks.size(); i += 2) {
            Variable<?> operand = resolveSingleOperand(blocks.get(i), CodeType.STRING, variables, output);
            result.append((String) operand.getValue());
        }
        return new Variable<>(result.toString(), CodeType.STRING);
    }

    /** Form 4: Integer via mathematische Operationen */
    public Variable<Integer> evaluateIntegerExpression(List<CodeBlock> blocks, Map<String, Variable<?>> variables, ExecutionLog output) {
        validateAlternatingPattern(blocks, ARITHMETIC_OPERATORS, "Integer expression", output);

        // Resolve every operand up front, left to right
        List<Integer> operands = new ArrayList<>();
        List<CodeType> operators = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i += 2) {
            operands.add((Integer) resolveSingleOperand(blocks.get(i), CodeType.INT, variables, output).getValue());
            if (i + 1 < blocks.size()) {
                operators.add(blocks.get(i + 1).getType());
            }
        }

        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(operands.get(0));

        for (int i = 0; i < operators.size(); i++) {
            CodeType op = operators.get(i);
            int nextOperand = operands.get(i + 1);

            switch (op) {
                case MULTIPLY:
                    stack.push(stack.pop() * nextOperand);
                    break;
                case DIVIDE:
                    if (nextOperand == 0) {
                        throw new IllegalArgumentException("Division by zero in integer expression");
                    }
                    stack.push(stack.pop() / nextOperand);
                    break;
                case ADD:
                    stack.push(nextOperand);
                    break;
                case SUBTRACT:
                    stack.push(-nextOperand);
                    break;
                default:
                    throw new IllegalStateException("Unreachable: " + op);
            }
        }

        int result = stack.stream().mapToInt(Integer::intValue).sum();
        return new Variable<>(result, CodeType.INT);
    }

    /** Liefert lineOfCode[index] oder wirft eine aussagekräftige Exception, falls die Zeile zu kurz ist. */
    public CodeBlock requireBlock(CodeBlock[] lineOfCode, int index, String errorContext, ExecutionLog output) {
        if (lineOfCode.length <= index) {
            throw new IllegalArgumentException(errorContext + " (Zeile hat nur " + lineOfCode.length + " Blöcke)");
        }
        return lineOfCode[index];
    }

    /** Prüft, ob die gegebene Variable bereits deklariert wurde */
    public boolean variableAlreadyDeclared(String varName, Map<String, Variable<?>> variables) {
        if (variables.containsKey(varName)) {
            return true;
        } else {
            return false;
        }
    }

    /** Erzeugt eine leere (uninitialisierte) Variable passend zum deklarierten Typ. */
    public Variable<?> createEmptyVariable(CodeType type, ExecutionLog output) {
        switch (type) {
            case STRING:
                return new Variable<>(null, CodeType.STRING);
            case INT:
                return new Variable<>(null, CodeType.INT);
            case BOOLEAN:
                return new Variable<>(null, CodeType.BOOLEAN);
            default:
                throw new IllegalArgumentException("Unbekannter Variablentyp: " + type);
        }
    }

    /**
     * Beschreibt den tatsächlich verwendeten Ausdruck einer Wertzuweisung (Rechenweg),
     * damit die Levelprüfung nicht nur das Endergebnis, sondern auch den Weg dahin abgleichen kann.
     * Liefert immer die gleiche Struktur (auch bei einem einzelnen Literal/einer einzelnen Variable),
     * damit die Vergleichslogik nicht zwischen Formen unterscheiden muss.
     */
    public Map<String, Object> describeValueAssignment(List<CodeBlock> valueAssignBlocks, CodeType variableType) {
        List<Map<String, Object>> operands = new ArrayList<>();
        List<String> operators = new ArrayList<>();

        for (CodeBlock block : valueAssignBlocks) {
            if (block instanceof ValueBlock valueBlock) {
                Map<String, Object> operand = new HashMap<>();
                operand.put("source", "LITERAL");
                operand.put("value", valueBlock.getValue().getValue());
                operands.add(operand);
            } else if (block instanceof VarNameBlock varNameBlock) {
                Map<String, Object> operand = new HashMap<>();
                operand.put("source", "VARIABLE");
                operand.put("variableName", varNameBlock.getName());
                operands.add(operand);
            } else {
                operators.add(block.getType().name());
            }
        }

        String expressionForm;
        if (operands.size() == 1) {
            expressionForm = "LITERAL".equals(operands.get(0).get("source")) ? "SINGLE_VALUE" : "VARIABLE_REFERENCE";
        } else if (variableType == CodeType.STRING) {
            expressionForm = "STRING_CONCATENATION";
        } else if (variableType == CodeType.INT) {
            expressionForm = "ARITHMETIC_EXPRESSION";
        } else {
            expressionForm = "UNKNOWN";
        }

        Map<String, Object> description = new HashMap<>();
        description.put("expressionForm", expressionForm);
        description.put("expressionOperands", operands);
        description.put("expressionOperators", operators);
        return description;
    }

}
