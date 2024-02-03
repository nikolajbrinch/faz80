package dk.nikolajbrinch.faz80.parser.values;

import dk.nikolajbrinch.faz80.parser.values.BooleanValue;
import dk.nikolajbrinch.faz80.parser.values.NumberValue;
import dk.nikolajbrinch.faz80.parser.values.StringValue;
import dk.nikolajbrinch.faz80.parser.values.Value;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class LogicUtil {

  public static Optional<Value<?>> binaryOperation(
      Value<?> left, Value<?> right, BinaryOperator<BooleanValue> function) {
    Optional<Value<?>> optional = Optional.empty();

    Value<?> l = convertToBoolean(left);
    Value<?> r = convertToBoolean(right);

    if (l instanceof BooleanValue lb && r instanceof BooleanValue rb) {
      optional = Optional.of(function.apply(lb, rb));
    }

    return optional;
  }

  public static Optional<Value<?>> unaryOperation(
      Value<?> value, UnaryOperator<BooleanValue> function) {
    Optional<Value<?>> optional = Optional.empty();

    Value<?> v = convertToBoolean(value);

    if (v instanceof BooleanValue vb) {
      optional = Optional.of(function.apply(vb));
    }

    return optional;
  }

  private static Value<?> convertToBoolean(Value<?> value) {
    Value<?> number = null;

    if (value instanceof StringValue stringValue && stringValue.canBeNUmber()) {
      number = stringValue.asNumberValue().asBooleanValue();
    } else if (value instanceof NumberValue numberValue) {
      number = numberValue.asNumberValue();
    }

    return number;
  }
}
