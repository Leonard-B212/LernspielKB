@Entity
public class IfBlock extends CodeBlock{
    private ExpressionBlock expression;
    private List<CodeBlock> program;

    public ExpressionBlock getExpression(){
        return expression;
    }

    public List<CodeBlock> getProgram(){
        return program;
    }
}