package dk.nikolajbrinch.assembler.parser.compiler;

public interface Value<T extends Value<T>> {

  public T add(T other);

  public StringValue asStringValue();

  public NumberValue asNumberValue();

  public Boolean asBoolean();

  Boolean compare(T other);
}
