package de.lernspiel.game.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true,
    defaultImpl = CodeBlock.class
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = VarNameBlock.class, name = "VAR_NAME"),
    @JsonSubTypes.Type(value = ValueBlock.class, name = "VALUE"),
    @JsonSubTypes.Type(value = IfStatementBlock.class, name = "IF_STATEMENT"),
    @JsonSubTypes.Type(value = ElseStatementBlock.class, name = "ELSE_STATEMENT")
})
public class CodeBlock {

    private CodeType type;

    public CodeType getType() {
        return type;
    }

    public void setType(CodeType type) {
        this.type = type;
    }
}