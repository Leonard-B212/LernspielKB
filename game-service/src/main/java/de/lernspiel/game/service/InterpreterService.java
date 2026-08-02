@service
public class InterpreterService {

    public List<String> run(ProgramRequest programRequest) {
        List<String> output = new ArrayList<>();

        for (CodeBlock block : programRequest.getProgram()) {
            executeBlock(block, output);
        }

        return output;
    }

    private void executeBlock(CodeBlock block, List<String> output) {
        switch (block.getType()) {
            case "A" -> { /* TODO */ }
            case "B" -> { /* TODO */ }
            default -> throw new IllegalArgumentException(
                    "Unknown block type: " + block.getType() + " (id " + block.getId() + ")");
        }
    }
    
}
