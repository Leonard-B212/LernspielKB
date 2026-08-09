package de.lernspiel.game.service;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
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

    public void variableDeclaration(CodeBlock[] lineOfCode, Map<String, Variable<?>> variables){
        CodeBlock variableType = lineOfCode[0];
        VarNameBlock variableName;
        ValueBlock variableValue;

        if(lineOfCode.length < 2){
            //TODO: Errorhandling
            throw new IllegalArgumentException("");
        } else if(!lineOfCode[1].getType().equals(CodeType.VAR_NAME)){
            //TODO: Errorhandling
            throw new IllegalArgumentException("");
        } else {
            variableName = (VarNameBlock) lineOfCode[1];
        }

        if(lineOfCode.length < 3){
            //TODO: Errorhandling
            throw new IllegalArgumentException("");
        } else if(lineOfCode[2].getType().equals(CodeType.BREAK)){
            //Variable Deklariert ohne Value Zuweisung

            if(variables.get(variableName.getName()).equals(null)){
                //TODO: Errorhandling
                throw new IllegalArgumentException("Variable bereits deklariert");
            }

            switch (variableType.getType()) {
                case STRING:
                    Variable<String> strVariable = new Variable<>(null, String.class);
                    variables.put(variableName.getName(), strVariable);
                    return;
                case INT:
                    Variable<Integer> intVariable = new Variable<>(null, Integer.class);
                    variables.put(variableName.getName(), intVariable);
                    return;
                case BOOLEAN:
                    Variable<Boolean> boolVariable = new Variable<>(null, Boolean.class);
                    variables.put(variableName.getName(), boolVariable);
                    return;
                default:
                    //TODO: Errorhandling
                    throw new IllegalArgumentException("");
            }
        }
            
        if(!lineOfCode[2].getType().equals(CodeType.EQUALS)){
            //TODO: Errorhandling
            throw new IllegalArgumentException("");
        } else if(lineOfCode.length < 4){
            //TODO: Errorhandling
            throw new IllegalArgumentException("");
        } else if(!lineOfCode[3].getType().equals(CodeType.VALUE)){
            //TODO: Errorhandling
            throw new IllegalArgumentException("");
        }
        
        variableValue = (ValueBlock) lineOfCode[3];

        if(lineOfCode.length < 5){
            //TODO: Errorhandling
            throw new IllegalArgumentException("");
        } else if(!lineOfCode[4].getType().equals(CodeType.BREAK)){
            //TODO: Errorhandling
            throw new IllegalArgumentException("");
        }

        //Variable deklariert und Value assigned
        if(variables.get(variableName.getName()).equals(null)){
            //TODO: Errorhandling
            throw new IllegalArgumentException("Variable bereits deklariert");
        }

        switch (variableType.getType()) {
            case STRING:
                if(variableValue.getValue().getType().equals(String.class)){
                    String value = (String) variableValue.getValue().getValue();
                    Variable<String> strVariable = new Variable<>(value, String.class);
                    variables.put(variableName.getName(), strVariable);
                    return;
                }
                //TODO: Errorhandling
                throw new IllegalArgumentException("");
            case INT:
                if(variableValue.getValue().getType().equals(Integer.class)){
                    Integer value = (Integer) variableValue.getValue().getValue();
                    Variable<Integer> intVariable = new Variable<>(value, Integer.class);
                    variables.put(variableName.getName(), intVariable);
                    return;
                }
                //TODO: Errorhandling
                throw new IllegalArgumentException("");
            case BOOLEAN:
                if(variableValue.getValue().getType().equals(Boolean.class)){
                    Boolean value = (Boolean) variableValue.getValue().getValue();
                    Variable<Boolean> boolVariable = new Variable<>(value, Boolean.class);
                    variables.put(variableName.getName(), boolVariable);
                    return;
                }
                //TODO: Errorhandling
                throw new IllegalArgumentException("");
            default:
                //TODO: Errorhandling
                throw new IllegalArgumentException("");
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
}
