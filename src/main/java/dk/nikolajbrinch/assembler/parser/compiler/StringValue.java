package dk.nikolajbrinch.assembler.parser.compiler;

import dk.nikolajbrinch.assembler.scanner.Token;

public record StringValue(String value, Type type) implements Value<StringValue> {

  enum Type {
    STRING,
    CHAR
  }

  public static StringValue create(Token token) {
    return new StringValue(unquote(token.text()),
        switch (token.type()) {
          case STRING -> Type.STRING;
          case CHAR -> Type.CHAR;
          default -> null;
        });
  }

  private static String unquote(String value) {
    if (value.startsWith("\"") && value.endsWith("\"")) {
      return removeQuotes(value);
    }

    if (value.startsWith("'") && value.endsWith("'")) {
      return removeQuotes(value);
    }

    return value;
  }

  private static String removeQuotes(String value) {
    return value.substring(1, value.length() - 1);
  }

  public StringValue add(StringValue other) {
    return new StringValue(value() + other.value(), Type.STRING);
  }

  @Override
  public StringValue asStringValue() {
    return type() == Type.CHAR ? new StringValue(value(), Type.STRING) : this;
  }

  @Override
  public NumberValue asNumberValue() {
    if (type() == Type.CHAR) {
      return NumberValue.create(value().getBytes()[0]);
    }

    throw new IllegalStateException("Value is not a character");
  }

  @Override
  public Boolean asBoolean() {
    return value().length() == 1 && value().getBytes()[0] == 0 ? Boolean.FALSE : Boolean.TRUE;
  }

  @Override
  public Boolean compare(StringValue other) {
    return value().equals(other.value());
  }

}
