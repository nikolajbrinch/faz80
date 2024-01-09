package dk.nikolajbrinch.assembler.compiler.values;

public interface Value<T extends Value<T>> {

  default T add(T other) {
    return null;
  }

  StringValue asStringValue();

  NumberValue asNumberValue();

  BooleanValue asBooleanValue();

  default BooleanValue compare(T other) {
    return new BooleanValue(false);
  }
}
