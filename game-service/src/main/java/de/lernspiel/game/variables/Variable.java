public interface Variable {
    Object getValue();
    String getTypeName();
}

public record IntVariable(int value) implements Variable {
    @Override public Object getValue() { return value; }
    @Override public String getTypeName() { return "int"; }
}

public record LongVariable(long value) implements Variable {
    @Override public Object getValue() { return value; }
    @Override public String getTypeName() { return "long"; }
}

public record DoubleVariable(double value) implements Variable {
    @Override public Object getValue() { return value; }
    @Override public String getTypeName() { return "double"; }
}

public record FloatVariable(float value) implements Variable {
    @Override public Object getValue() { return value; }
    @Override public String getTypeName() { return "float"; }
}

public record BooleanVariable(boolean value) implements Variable {
    @Override public Object getValue() { return value; }
    @Override public String getTypeName() { return "boolean"; }
}

public record CharVariable(char value) implements Variable {
    @Override public Object getValue() { return value; }
    @Override public String getTypeName() { return "char"; }
}

public record ByteVariable(byte value) implements Variable {
    @Override public Object getValue() { return value; }
    @Override public String getTypeName() { return "byte"; }
}

public record ShortVariable(short value) implements Variable {
    @Override public Object getValue() { return value; }
    @Override public String getTypeName() { return "short"; }
}

public record StringVariable(String value) implements Variable {
    @Override public Object getValue() { return value; }
    @Override public String getTypeName() { return "String"; }
}

/**
 * Represents Java's null literal. A separate type rather than
 * StringVariable(null) or similar, so getTypeName() stays meaningful
 * and callers don't need to null-check getValue() to know what they have.
 */
public record NullVariable() implements Variable {
    @Override public Object getValue() { return null; }
    @Override public String getTypeName() { return "null"; }
}