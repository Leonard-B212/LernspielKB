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
import de.lernspiel.game.dto.CodeType;
import de.lernspiel.game.dto.ProgramRequest;
import de.lernspiel.game.dto.ValueBlock;
import de.lernspiel.game.dto.VarNameBlock;
import de.lernspiel.game.dto.Variable;

@Service
public class InterpreterService {
    private static final Set<CodeType> ARITHMETIC_OPERATORS = EnumSet.of(CodeType.ADD, CodeType.SUBTRACT, CodeType.MULTIPLY, CodeType.DIVIDE);
    private static final Set<CodeType> STRING_CONCAT_OPERATORS = EnumSet.of(CodeType.ADD);

    public List<String> run(ProgramRequest programRequest) {
        List<String> output = new ArrayList<String>();
        switch(programRequest.getLanguageId()){
            case 1: //Id für Java
                interpreterMainJava(programRequest);
                break;
            case 2: //Id für Python
                //TODO: Python Interpreter
                throw new UnsupportedOperationException("Python interpreter not implemented yet");
            default: //Id nicht implementiert
                throw new IllegalArgumentException("Unknown language id: " + programRequest.getLanguageId());
        }
        return output;
    }

    public void interpreterMainJava(ProgramRequest programRequest){
        Map<String, Variable<?>> variables = new HashMap<>();
        executeProgram(programRequest.getProgram(), variables);
    }

    public void executeProgram(List<CodeBlock> program, Map<String, Variable<?>> variables){
        List<CodeBlock[]> parsedCode = parseCode(program);
        for(CodeBlock[] lineOfCode : parsedCode){
            executeCode(lineOfCode, variables);
        }
    }

    public void executeCode(CodeBlock[] lineOfCode, Map<String, Variable<?>> variables){
        CodeBlock firstBlock = lineOfCode[0];
        switch (firstBlock.getType()) {
            case STRING, INT, BOOLEAN:
                variableDeclaration(lineOfCode, variables);
                break;
            case VAR_NAME:
                //Variable Value Assignment
                break;
            case IF_STATEMENT:
                //Conditional Statement
                break;
            default:
                //TODO: Errorhandling
                break;
        }
    }

    public List<CodeBlock[]> parseCode(List<CodeBlock> program) {
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
            result.add(current.toArray(new CodeBlock[0]));
        }

        return result;
    }

    /**
     * Verarbeitet eine Variablendeklaration, z. B.:
     *   int x;          -> Deklaration ohne Initialwert
     *   int x = 5;       -> Deklaration mit Initialwert
     */
    public void variableDeclaration(CodeBlock[] lineOfCode, Map<String, Variable<?>> variables) {
        System.out.println("variableDeclaration gestartet mit " + lineOfCode.length + " Blöcken");

        CodeBlock variableTypeBlock = lineOfCode[0];
        VarNameBlock variableNameBlock = extractVariableName(lineOfCode);
        String varName = variableNameBlock.getName();

        ensureNotAlreadyDeclared(varName, variables);

        CodeBlock thirdBlock = requireBlock(lineOfCode, 2, "Erwarte '=' oder ';' nach dem Variablennamen");

        if (thirdBlock.getType().equals(CodeType.BREAK)) {
            // Fall 1: Deklaration ohne Wertzuweisung, z. B. "int x;"
            Variable<?> emptyVariable = createEmptyVariable(variableTypeBlock.getType());
            variables.put(varName, emptyVariable);
            System.out.println("Variable " + varName + " " + variableTypeBlock.getType() + " ohne Initialwert deklariert");
            return;
        }

        if (!thirdBlock.getType().equals(CodeType.EQUALS)) {
            throw new IllegalArgumentException(
                "Erwarte '=' oder ';' nach dem Variablennamen, war aber: " + thirdBlock.getType());
        }

        
        //Fall 2: Deklaration mit Wertzuweisung
        List<CodeBlock> valueAssignBlocks = new ArrayList<>();
        for(int i = 3; i < lineOfCode.length - 1; i++){
            valueAssignBlocks.add(requireBlock(lineOfCode, i, "Erwarte eine Wertzuweisung nach '='"));
        }

        if(valueAssignBlocks.isEmpty()){
            throw new IllegalArgumentException("Erwarte eine Wertzuweisung nach '='");
        }

        Variable<?> initializedVariable = determineVariableValue(valueAssignBlocks, variables, variableTypeBlock.getType());

        CodeBlock terminator = requireBlock(lineOfCode, lineOfCode.length - 1, "Erwarte ';' am Ende der Deklaration");
        if (!terminator.getType().equals(CodeType.BREAK)) {
            throw new IllegalArgumentException(
                "Erwarte ';' am Ende der Deklaration, war aber: " + terminator.getType());
        }

        variables.put(varName, initializedVariable);
        System.out.println("Variable " + varName + " " + variableTypeBlock.getType() + " mit Initialwert deklariert: " + initializedVariable.getValue());
    }

    /**
     * Resolves the value(s) between '=' and ';' into a single Variable, according to the four supported assignment forms:
     *   1. a single ValueBlock
     *   2. a single VarNameBlock referencing an already-declared variable of the same type
     *   3. (STRING only) operand ADD operand ADD operand ...
     *   4. (INT only)    operand OP operand OP operand ...  where OP in {ADD, SUBTRACT, MULTIPLY, DIVIDE}
     */
    public Variable<?> determineVariableValue(List<CodeBlock> valueAssignBlocks, Map<String, Variable<?>> variables, CodeType type) {
        if (valueAssignBlocks.size() == 1) {
            // Form 1 or 2: a single value or a single variable reference
            return resolveSingleOperand(valueAssignBlocks.get(0), type, variables);
        }

        // More than one block only makes sense for STRING (concatenation) or INT (arithmetic)
        switch (type) {
            case STRING:
                return evaluateStringConcatenation(valueAssignBlocks, variables);
            case INT:
                return evaluateIntegerExpression(valueAssignBlocks, variables);
            case BOOLEAN:
                throw new IllegalArgumentException("Boolean variables only support a single value or variable reference");
            default:
                throw new IllegalArgumentException("Unknown variable type: " + type);
        }
    }

    /** Resolves a single operand (ValueBlock or VarNameBlock) into a Variable of the expected type. */
    public Variable<?> resolveSingleOperand(CodeBlock block, CodeType expectedType, Map<String, Variable<?>> variables) {
        if (block instanceof ValueBlock valueBlock) {
            return requireMatchingType(valueBlock.getValue(), javaClassFor(expectedType));
        }
        if (block instanceof VarNameBlock varNameBlock) {
            return resolveVariableReference(varNameBlock.getName(), expectedType, variables);
        }
        throw new IllegalArgumentException("Expected a value or variable name, got: " + block.getType());
    }

    /**
     * Looks up a variable by name and validates it's actually usable:
     * - must be declared
     * - must actually have a value (catches "int x; y = x;" - using a declared-but-uninitialized variable, which would otherwise NPE silently during concatenation/arithmetic below)
     * - must match the expected type
     */
    public Variable<?> resolveVariableReference(String name, CodeType expectedType, Map<String, Variable<?>> variables) {
        if (!variables.containsKey(name)) {
            throw new IllegalArgumentException("Use of undeclared variable: " + name);
        }
        Variable<?> referenced = variables.get(name);
        if (referenced.getValue() == null) {
            throw new IllegalArgumentException("Use of declared but uninitialized variable: " + name);
        }
        return requireMatchingType(referenced, javaClassFor(expectedType));
    }

    public Class<?> javaClassFor(CodeType type) {
        switch (type) {
            case STRING:  return String.class;
            case INT:     return Integer.class;
            case BOOLEAN: return Boolean.class;
            default:      throw new IllegalArgumentException("No Java type mapped for: " + type);
        }
    }

    public <T> Variable<T> requireMatchingType(Variable<?> value, Class<T> expected) {
        if (!value.getType().equals(expected)) {
            throw new IllegalArgumentException("Type mismatch: expected " + expected.getSimpleName() + ", but value was of type " + value.getType().getSimpleName());
        }
        T castValue = expected.cast(value.getValue());
        return new Variable<>(castValue, expected);
    }

    /** Validates that blocks strictly alternate operand-operator-operand-...-operand. Catches malformed chains like "1 ADD ADD 2" or "1 ADD" (trailing operator) */
    public void validateAlternatingPattern(List<CodeBlock> blocks, Set<CodeType> allowedOperators, String context) {
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
    public Variable<String> evaluateStringConcatenation(List<CodeBlock> blocks, Map<String, Variable<?>> variables) {
        validateAlternatingPattern(blocks, STRING_CONCAT_OPERATORS, "String concatenation");

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < blocks.size(); i += 2) {
            Variable<?> operand = resolveSingleOperand(blocks.get(i), CodeType.STRING, variables);
            result.append((String) operand.getValue());
        }

        System.out.println("String concatenation result: \"" + result + "\"");
        return new Variable<>(result.toString(), String.class);
    }

    /** Form 4: Integer via mathematische Operationen */
    public Variable<Integer> evaluateIntegerExpression(List<CodeBlock> blocks, Map<String, Variable<?>> variables) {
        validateAlternatingPattern(blocks, ARITHMETIC_OPERATORS, "Integer expression");

        // Resolve every operand up front, left to right
        List<Integer> operands = new ArrayList<>();
        List<CodeType> operators = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i += 2) {
            operands.add((Integer) resolveSingleOperand(blocks.get(i), CodeType.INT, variables).getValue());
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
                case MULTIPLY -> stack.push(stack.pop() * nextOperand);
                case DIVIDE -> {
                    if (nextOperand == 0) {
                        throw new IllegalArgumentException("Division by zero in integer expression");
                    }
                    stack.push(stack.pop() / nextOperand);
                }
                case ADD -> stack.push(nextOperand);
                case SUBTRACT -> stack.push(-nextOperand);
                default -> throw new IllegalStateException("Unreachable: " + op);
            }
        }

        int result = stack.stream().mapToInt(Integer::intValue).sum();
        System.out.println("Integer expression evaluated to: " + result);
        return new Variable<>(result, Integer.class);
    }

    /** Prüft und liefert den Variablennamen-Block an Position 1. */
    private VarNameBlock extractVariableName(CodeBlock[] lineOfCode) {
        CodeBlock block = requireBlock(lineOfCode, 1, "Erwarte einen Variablennamen an Position 1");
        if (!block.getType().equals(CodeType.VAR_NAME)) {
            throw new IllegalArgumentException("Erwarte VAR_NAME an Position 1, war aber: " + block.getType());
        }
        return (VarNameBlock) block;
    }

    /** Liefert lineOfCode[index] oder wirft eine aussagekräftige Exception, falls die Zeile zu kurz ist. */
    private CodeBlock requireBlock(CodeBlock[] lineOfCode, int index, String errorContext) {
        if (lineOfCode.length <= index) {
            throw new IllegalArgumentException(errorContext + " (Zeile hat nur " + lineOfCode.length + " Blöcke)");
        }
        return lineOfCode[index];
    }

    /** Prüft, ob die zu deklarierende Variable bereits existiert */
    private void ensureNotAlreadyDeclared(String varName, Map<String, Variable<?>> variables) {
        if (variables.containsKey(varName)) {
            throw new IllegalArgumentException("Variable bereits deklariert: " + varName);
        }
    }

    /** Erzeugt eine leere (uninitialisierte) Variable passend zum deklarierten Typ. */
    private Variable<?> createEmptyVariable(CodeType type) {
        switch (type) {
            case STRING:
                return new Variable<>(null, String.class);
            case INT:
                return new Variable<>(null, Integer.class);
            case BOOLEAN:
                return new Variable<>(null, Boolean.class);
            default:
                throw new IllegalArgumentException("Unbekannter Variablentyp: " + type);
        }
    }
}
