package dk.nikolajbrinch.faz80.parser.base.values;

import dk.nikolajbrinch.faz80.parser.base.values.StringValue.Type;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

/**
 * Represents a number value in the assembler
 *
 * TODO: Add support for Little Endian and Big Endian
 * 
 * @param value
 * @param size
 */
public record NumberValue(long value, Size size) implements Value<NumberValue> {

  public static NumberValue create(final long value) {
    return create(value, null);
  }

  public static NumberValue createWord(final long value) {
    return create(value, Size.WORD);
  }

  public static NumberValue create(final AssemblerToken token) {
    return create(token, null);
  }

  public static NumberValue createWord(final AssemblerToken token) {
    return create(token, Size.WORD);
  }

  private static NumberValue create(final long value, final Size size) {
    return fromDecimal(value, size);
  }

  private static NumberValue create(final AssemblerToken token, final Size size) {
    return switch (token.type()) {
      case DECIMAL_NUMBER -> fromDecimal(token, size);
      case HEX_NUMBER -> fromHex(token, size);
      case OCTAL_NUMBER -> fromOctal(token, size);
      case BINARY_NUMBER -> fromBinary(token, size);
      default -> null;
    };
  }

  private static NumberValue fromDecimal(final AssemblerToken token, final Size size) {
    return fromDecimal(Long.parseLong(filterNumber(token.text())), size);
  }

  private static NumberValue fromDecimal(final long value, final Size size) {
    if (value <= 0xFFL && value >= Byte.MIN_VALUE) {
      return newNumber(value, Size.BYTE, size);
    }

    if (value <= 0xFFFFL && value >= Short.MIN_VALUE) {
      return newNumber(value, Size.WORD, size);
    }

    if (value <= 0xFFFFFFFFL && value >= Integer.MIN_VALUE) {
      return newNumber(value, Size.WORD, size);
    }

    throw new IllegalStateException("Decimal number too large");
  }

  private static NumberValue fromHex(final AssemblerToken token, final Size size) {
    String number = filterHex(token.text());

    Size actualSize =
        switch (number.length()) {
          case 1, 2 -> Size.BYTE;
          case 3 -> number.charAt(0) == '0' ? Size.BYTE : Size.WORD;
          case 4 -> Size.WORD;
          case 5 -> number.charAt(0) == '0' ? Size.WORD : Size.LONG;
          case 6, 7, 8 -> Size.LONG;
          default -> null;
        };

    if (actualSize != null) {
      return newNumber(Long.parseLong(number, 16), actualSize, size);
    }

    throw new IllegalStateException("Hexadecimal number too large");
  }

  private static NumberValue fromOctal(final AssemblerToken token, final Size size) {
    String number = filterNumber(token.text());
    int length = number.length();
    long value = Long.parseLong(number);

    if (length <= 3) {
      if (value <= 0xFFL && value >= Byte.MIN_VALUE) {
        return newNumber(value, Size.BYTE, size);
      }

      return newNumber(value, Size.WORD, size);
    }

    if (length <= 6) {
      if (value <= 0xFFFFL && value >= Short.MIN_VALUE) {
        return newNumber(value, Size.WORD, size);
      }

      return newNumber(value, Size.LONG, size);
    }

    if (length <= 11 && (value <= 0xFFFFFFFFL && value >= Integer.MIN_VALUE)) {
      return newNumber(value, Size.WORD, size);
    }

    throw new IllegalStateException("Octal number too large");
  }

  private static NumberValue fromBinary(final AssemblerToken token, final Size size) {
    String number = filterNumber(token.text());
    int length = number.length();
    long value = Long.parseLong(number, 2);

    if (length <= 8) {
      return newNumber(value, Size.BYTE, size);
    }

    if (length <= 16) {
      return newNumber(value, Size.WORD, size);
    }

    if (length <= 32) {
      return newNumber(value, Size.LONG, size);
    }

    throw new IllegalStateException("Binary number too large");
  }

  public static NumberValue twosComplement(NumberValue normal) {
    return new NumberValue(twosComplement(normal.value()), normal.size());
  }

  public static long twosComplement(long normal) {
    return normal < 0L ? (Math.abs(normal) ^ 0b11111111) + 1 : normal;
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
      case UNKNOWN -> throw new IllegalSizeException("Number has unknown size!");
      case BYTE, WORD -> new NumberValue((value() >> 8) & 0xFFL, Size.BYTE);
      case LONG -> new NumberValue((value() >> 24) & 0xFFL, Size.BYTE);
    };
  }

  public NumberValue msw() {
    return switch (size()) {
      case UNKNOWN -> throw new IllegalSizeException("Number has unknown size!");
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

  public BooleanValue asBooleanValue() {
    return new BooleanValue(value() != 0);
  }

  @Override
  public BooleanValue compare(NumberValue other) {
    return new BooleanValue(value() == other.value());
  }

  private Size size(NumberValue other) {
    return Size.values()[Math.max(size().ordinal(), other.size().ordinal())];
  }

  public enum Size {
    UNKNOWN,
    BYTE,
    WORD,
    LONG
  }

  private static NumberValue newNumber(long value, final Size size, final Size sizeOverride) {
    Size actualSize = sizeOverride == null ? size : sizeOverride;

    return new NumberValue(
        switch (actualSize) {
          case BYTE -> value & 0xFFL;
          case WORD -> value & 0xFFFFL;
          case LONG -> value & 0xFFFFFFFFL;
          default -> value;
        },
        actualSize);
  }

  private static String filterNumber(String text) {
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < text.length(); i++) {
      if (Character.isDigit(text.charAt(i))) {
        builder.append(text.charAt(i));
      }
    }

    return builder.toString();
  }

  private static String filterHex(String text) {
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < text.length(); i++) {
      if (Character.isDigit(text.charAt(i))
          || text.charAt(i) == 'a'
          || text.charAt(i) == 'A'
          || text.charAt(i) == 'b'
          || text.charAt(i) == 'B'
          || text.charAt(i) == 'c'
          || text.charAt(i) == 'C'
          || text.charAt(i) == 'd'
          || text.charAt(i) == 'D'
          || text.charAt(i) == 'e'
          || text.charAt(i) == 'E'
          || text.charAt(i) == 'f'
          || text.charAt(i) == 'F') {
        builder.append(text.charAt(i));
      }
    }

    return builder.toString();
  }
}
