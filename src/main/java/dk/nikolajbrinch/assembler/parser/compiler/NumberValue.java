package dk.nikolajbrinch.assembler.parser.compiler;

import dk.nikolajbrinch.assembler.parser.compiler.StringValue.Type;
import dk.nikolajbrinch.assembler.scanner.Token;

public record NumberValue(long value, Size size) implements Value<NumberValue> {

  enum Size {
    BYTE,
    WORD,
    LONG
  }

  public static NumberValue create(int value) {
    return fromDecimal(value);
  }

  public static NumberValue create(Token token) {
    return switch (token.type()) {
      case DECIMAL_NUMBER -> fromDecimal(token);
      case HEX_NUMBER -> fromHex(token);
      case OCTAL_NUMBER -> fromOctal(token);
      case BINARY_NUMBER -> fromBinary(token);
      default -> null;
    };
  }

  private static NumberValue fromDecimal(Token token) {
    return fromDecimal(Long.parseLong(token.text()));
  }

  private static NumberValue fromDecimal(long value) {
    if (value <= 0xFFL && value >= Byte.MIN_VALUE) {
      return new NumberValue(value, Size.BYTE);
    }

    if (value <= 0xFFFFL && value >= Short.MIN_VALUE) {
      return new NumberValue(value, Size.WORD);
    }

    if (value <= 0xFFFFFFFFL && value >= Integer.MIN_VALUE) {
      return new NumberValue(value, Size.WORD);
    }

    throw new IllegalStateException("Decimal number too large");
  }

  private static NumberValue fromHex(Token token) {
    Size size = switch (token.text().length()) {
      case 1, 2 -> Size.BYTE;
      case 3, 4 -> Size.WORD;
      case 5, 6, 7, 8 -> Size.LONG;
      default -> null;
    };

    if (size != null) {
      return new NumberValue(Long.parseLong(token.text(), 16), size);
    }

    throw new IllegalStateException("Hexadecimal number too large");
  }

  private static NumberValue fromOctal(Token token) {
    int length = token.text().length();
    long value = Long.parseLong(token.text());

    if (length <= 3) {
      if (value <= 0xFFL && value >= Byte.MIN_VALUE) {
        return new NumberValue(value, Size.BYTE);
      }

      return new NumberValue(value, Size.WORD);
    }

    if (length <= 6) {
      if (value <= 0xFFFFL && value >= Short.MIN_VALUE) {
        return new NumberValue(value, Size.WORD);
      }

      return new NumberValue(value, Size.LONG);
    }

    if (length <= 11 && (value <= 0xFFFFFFFFL && value >= Integer.MIN_VALUE)) {
      return new NumberValue(value, Size.WORD);
    }

    throw new IllegalStateException("Octal number too large");
  }

  private static NumberValue fromBinary(Token token) {
    int length = token.text().length();
    long value = Long.parseLong(token.text());

    if (length <= 8) {
      return new NumberValue(value, Size.BYTE);
    }

    if (length <= 16) {
      return new NumberValue(value, Size.WORD);
    }

    if (length <= 32) {
      return new NumberValue(value, Size.LONG);
    }

    throw new IllegalStateException("Binary number too large");
  }

  public NumberValue subtract(NumberValue other) {
    return new NumberValue(value() - other.value(), size(other));
  }


  public NumberValue add(NumberValue other) {
    return new NumberValue(value() + other.value(), size(other));
  }

  public NumberValue divide(NumberValue other) {
    return new NumberValue(value() / other.value(), size(other));
  }

  public NumberValue multiply(NumberValue other) {
    return new NumberValue(value() * other.value(), size(other));
  }

  public NumberValue negate() {
    return new NumberValue(-value(), size());
  }

  public NumberValue bitwiseAnd(NumberValue other) {
    return new NumberValue(value() & other.value(), size(other));
  }

  public NumberValue bitwiseOr(NumberValue other) {
    return new NumberValue(value() | other.value(), size(other));
  }

  public NumberValue bitwiseXor(NumberValue other) {
    return new NumberValue(value() ^ other.value(), size(other));
  }

  public NumberValue bitwiseNot() {
    return new NumberValue(~value(), size());
  }

  public NumberValue bitwiseShiftLeft(NumberValue other) {
    return new NumberValue(value() << other.value(), size(other));
  }

  public NumberValue bitwiseShiftRigth(NumberValue other) {
    return new NumberValue(value() >> other.value(), size(other));
  }

  public NumberValue logicalShiftRight(NumberValue other) {
    return new NumberValue(value() >>> other.value(), size(other));
  }

  public NumberValue asWord() {
    return new NumberValue(value() & 0xFFFFL, Size.WORD);
  }

  public NumberValue asByte() {
    return new NumberValue(value() & 0xFFL, Size.BYTE);
  }

  public NumberValue asLong() {
    return new NumberValue(value() & 0xFFFFFFFFL, Size.LONG);
  }

  public NumberValue lsb() {
    return new NumberValue(value() & 0xFFL, Size.BYTE);
  }

  public NumberValue lsw() {
    return new NumberValue(value() & 0xFFFFL, Size.WORD);
  }

  public NumberValue msb() {
    return switch (size()) {
      case BYTE -> lsb();
      case WORD -> new NumberValue((value() >> 8) & 0xFFL, Size.BYTE);
      case LONG -> new NumberValue((value() >> 24) & 0xFFL, Size.BYTE);
    };
  }

  public NumberValue msw() {
    return switch (size()) {
      case BYTE -> lsw();
      case WORD -> lsw();
      case LONG -> new NumberValue((value() >> 16) & 0xFFFFL, Size.WORD);
    };
  }

  public StringValue asStringValue() {
    return new StringValue(String.valueOf(value()), Type.STRING);
  }

  @Override
  public NumberValue asNumberValue() {
    return this;
  }

  public Boolean asBoolean() {
    return value() == 0 ? Boolean.FALSE : Boolean.TRUE;
  }

  @Override
  public Boolean compare(NumberValue other) {
    return value() == other.value();
  }

  private Size size(NumberValue other) {
    return Size.values()[Math.max(size().ordinal(), other.size().ordinal())];
  }
}
