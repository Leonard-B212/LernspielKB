import java.util.ArrayList;

@service
public class InterpreterService {

    public List<String> run(ProgramRequest programRequest) {
        ArrayList<String> output = new ArrayList<String>();
        switch(programRequest.getLanguageId){
            case 1: //Id für Java
                interpreterMainJava(programRequest);
                break;
            case 2: //Id für Python
                interpreterMainPython(programRequest);
                break;
            default: //Id nicht implementiert
                //TODO: Errorhandling
                break;
        }
        return output;
    }

    public void interpreterMainJava(ProgramRequest programRequest){

    }

    public void interpreterMainPython(ProgramRequest programRequest){
        
    }
}
