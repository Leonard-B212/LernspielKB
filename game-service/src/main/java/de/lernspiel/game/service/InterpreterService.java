import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

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
        Map<String, Variable> variables = new HashMap<>();
        executeCode(programRequest.getProgram(), variables);
    }

    public void executeCode(List<CodeBlock> program, Map<String, Variable> variables){
        for(CodeBlock cb: program){
            executeCodeBlock(cb, variables);
        }
    }

    public void executeCodeBlock(CodeBlock codeBlock, Map<String, Variable> variables){
        switch(codeBlock.getType()){
            case 1: //If-Statement
                IfBlock cb = (IfBlock) codeBlock;
                if(checkExpression(cb.getExpression(), variables)){
                    executeCode(cb.getProgram(), variables);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown block type: " + codeBlock.getType());
        }
    }

    public boolean checkExpression(ExpressionBlock expression, Map<String, Variable> variables){
        return true; //TODO
    }
}
