package dk.nikolajbrinch.assembler.compiler;

public interface Value<T extends Value<T>> {

  public T add(T other);

  public StringValue asStringValue();

  public NumberValue asNumberValue();

  public BooleanValue asBooleanValue();

  BooleanValue compare(T other);
}
