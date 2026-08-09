package de.lernspiel.game.service;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

import de.lernspiel.game.dto.ProgramRequest;
import de.lernspiel.game.entity.CodeBlock;
import de.lernspiel.game.entity.CodeType;
import de.lernspiel.game.entity.Variable;

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
            executeCode(lineOfCode, variables);;
        }
    }

    public void executeCode(CodeBlock[] lineOfCode, Map<String, Variable<?>> variables){
        
    }

    private List<CodeBlock[]> parseCode(List<CodeBlock> program) {
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
            result.add(current.toArray(new CodeBlock[0])); // trailing blocks with no terminating 0
        }

        return result;
    }
}
