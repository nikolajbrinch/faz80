package dk.nikolajbrinch.assembler.compiler.values;

public interface Value<T extends Value<T>> {

  T add(T other);

  StringValue asStringValue();

  NumberValue asNumberValue();

  BooleanValue asBooleanValue();

  BooleanValue compare(T other);
}
