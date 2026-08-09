package de.lernspiel.game.service;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.lernspiel.game.dto.CodeBlock;
import de.lernspiel.game.dto.CodeType;
import de.lernspiel.game.dto.ProgramRequest;
import de.lernspiel.game.dto.ValueBlock;
import de.lernspiel.game.dto.VarNameBlock;
import de.lernspiel.game.dto.Variable;

@Service
public class InterpreterService {

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
                "Erwarte '=' nach dem Variablennamen, war aber: " + thirdBlock.getType());
        }

        // Fall 2: Deklaration mit Wertzuweisung, z. B. "int x = 5;"
        CodeBlock valueBlockRaw = requireBlock(lineOfCode, 3, "Erwarte einen Wert nach '='");
        if (!valueBlockRaw.getType().equals(CodeType.VALUE)) {
            throw new IllegalArgumentException(
                "Erwarte einen Wert nach '=', war aber: " + valueBlockRaw.getType());
        }
        ValueBlock valueBlock = (ValueBlock) valueBlockRaw;

        CodeBlock terminator = requireBlock(lineOfCode, 4, "Erwarte ';' am Ende der Deklaration");
        if (!terminator.getType().equals(CodeType.BREAK)) {
            throw new IllegalArgumentException(
                "Erwarte ';' am Ende der Deklaration, war aber: " + terminator.getType());
        }

        Variable<?> initializedVariable = createInitializedVariable(variableTypeBlock.getType(), valueBlock);
        variables.put(varName, initializedVariable);
        System.out.println("Variable " + varName + " " + variableTypeBlock.getType() + " mit Initialwert deklariert: " + initializedVariable.getValue());
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

    /** Erzeugt eine initialisierte Variable und prüft dabei, dass der Werttyp zum deklarierten Typ passt. */
    private Variable<?> createInitializedVariable(CodeType type, ValueBlock valueBlock) {
        Variable<?> parsedValue = valueBlock.getValue();
        if (parsedValue == null) {
            throw new IllegalArgumentException("ValueBlock enthält keinen Wert");
        }

        switch (type) {
            case STRING:
                return requireMatchingType(parsedValue, String.class);
            case INT:
                return requireMatchingType(parsedValue, Integer.class);
            case BOOLEAN:
                return requireMatchingType(parsedValue, Boolean.class);
            default:
                throw new IllegalArgumentException("Unbekannter Variablentyp: " + type);
        }
    }

    /** Prüft Typ-Übereinstimmung und castet typsicher via Class#cast statt manuellem (T)-Cast pro Fall. */
    private <T> Variable<T> requireMatchingType(Variable<?> value, Class<T> expected) {
        if (!value.getType().equals(expected)) {
            throw new IllegalArgumentException(
                "Typ-Mismatch: erwartet " + expected.getSimpleName()
                + ", aber Wert war vom Typ " + value.getType().getSimpleName());
        }
        T castValue = expected.cast(value.getValue());
        return new Variable<>(castValue, expected);
    }
}
