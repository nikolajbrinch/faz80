package dk.nikolajbrinch.assembler.compiler.values;

public final class IntegerMath {

  public static Value<?> divide(Value<?> left, Value<?> right) {
    return MathUtil.binaryOperation(left, right, NumberValue::divide)
        .orElseThrow(
            () -> new IllegalMathOperationException("Operands for division are not integers"));
  }

  public static Value<?> multiply(Value<?> left, Value<?> right) {
    return MathUtil.binaryOperation(left, right, NumberValue::multiply)
        .orElseThrow(
            () ->
                new IllegalMathOperationException("Operands for multiplication are not integers"));
  }

  public static Value<?> subtract(Value<?> left, Value<?> right) {
    return MathUtil.binaryOperation(left, right, NumberValue::subtract)
        .orElseThrow(
            () -> {
              return new IllegalMathOperationException("Operands for subtraction are not integers");
            });
  }

  public static Value<?> add(Value<?> left, Value<?> right) {
    return MathUtil.binaryOperation(left, right, NumberValue::subtract)
        .orElseGet(() -> left.asStringValue().add(right.asStringValue()));
  }

  public static Value<?> negate(Value<?> value) {
    return MathUtil.unaryOperation(value, NumberValue::negate)
        .orElseThrow(
            () -> new IllegalMathOperationException("Operands for negation are not integers"));
  }
}
