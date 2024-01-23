package dk.nikolajbrinch.assembler.compiler.values;

import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class MathUtil {

  public static Optional<Value<?>> binaryOperation(
      Value<?> left, Value<?> right, BinaryOperator<NumberValue> function) {
    Optional<Value<?>> optional = Optional.empty();

    Value<?> l = convertToNumber(left);
    Value<?> r = convertToNumber(right);

    if (l instanceof NumberValue ln && r instanceof NumberValue rn) {
      optional = Optional.of(function.apply(ln, rn));
    }

    return optional;
  }

  public static Optional<Value<?>> unaryOperation(
      Value<?> value, UnaryOperator<NumberValue> function) {
    Optional<Value<?>> optional = Optional.empty();

    Value<?> v = convertToNumber(value);

    if (v instanceof NumberValue vn) {
      optional = Optional.of(function.apply(vn));
    }

    return optional;
  }

  private static Value<?> convertToNumber(Value<?> value) {
    Value<?> number = value;

    if (value instanceof StringValue stringValue && stringValue.canBeNUmber()) {
      number = stringValue.asNumberValue();
    } else if (value instanceof BooleanValue booleanValue) {
      number = booleanValue.asNumberValue();
    }

    return number;
  }
}
