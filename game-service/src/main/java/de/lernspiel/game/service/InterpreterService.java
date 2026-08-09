package de.lernspiel.game.service;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

import de.lernspiel.game.dto.CodeBlock;
import de.lernspiel.game.dto.CodeType;
import de.lernspiel.game.dto.ProgramRequest;
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
        CodeBlock variableName;
        CodeBlock variableValue;

        if(lineOfCode.length < 2){
            //TODO: Errorhandling
        } else if(!lineOfCode[1].getType().equals(CodeType.VAR_NAME)){
            //TODO: Errorhandling
        } else {
            variableName = lineOfCode[1];
        }

        if(lineOfCode.length < 3){
            //TODO: Errorhandling
        } else if(lineOfCode[2].getType().equals(CodeType.BREAK)){
            //TODO: Variable Deklariert ohne Value Zuweisung
        } else if(!lineOfCode[2].getType().equals(CodeType.EQUALS)){
            //TODO: Errorhandling
        } else {
            if(lineOfCode.length < 4){
                //TODO: Errorhandling
            } else if(!lineOfCode[3].getType().equals(CodeType.VALUE)){
                //TODO: Errorhandling
            } else {
                variableValue = lineOfCode[3];
                if(lineOfCode.length < 5){
                    //TODO: Errorhandling
                } else if(!lineOfCode[4].getType().equals(CodeType.BREAK)){
                    //TODO: Errorhandling
                } else {
                    //TODO: Variable deklariert und Value assigned
                }
            }
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
