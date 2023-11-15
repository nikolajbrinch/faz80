package dk.nikolajbrinch.assembler.compiler.values;

import dk.nikolajbrinch.assembler.compiler.values.StringValue.Type;

public record BooleanValue(boolean value) implements Value<BooleanValue> {

  @Override
  public BooleanValue add(BooleanValue other) {
    return new BooleanValue(value || other.value);
  }

  @Override
  public StringValue asStringValue() {
    return new StringValue(Boolean.toString(value), Type.STRING);
  }

  @Override
  public NumberValue asNumberValue() {
    return NumberValue.create(value ? 1 : 0);
  }

  @Override
  public BooleanValue asBooleanValue() {
    return this;
  }

  @Override
  public BooleanValue compare(BooleanValue other) {
    return new BooleanValue(value == other.value);
  }

  public BooleanValue not() {
    return new BooleanValue(!value);
  }

  public BooleanValue and(BooleanValue other) {
    return new BooleanValue(value && other.value());
  }

  public BooleanValue or(BooleanValue other) {
    return new BooleanValue(value || other.value());
  }
}
