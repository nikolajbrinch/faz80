package dk.nikolajbrinch.faz80.parser.values;

public final class Logic {

  public static Value<?> not(Value<?> value) {
    return LogicUtil.unaryOperation(value, BooleanValue::not)
        .orElseThrow(() -> new IllegalLogicOperationException("Operands for not are not integers"));
  }

  public static Value<?> and(Value<?> left, Value<?> right) {
    return LogicUtil.binaryOperation(left, right, BooleanValue::and)
        .orElseThrow(() -> new IllegalLogicOperationException("Operands for and are not integers"));
  }

  public static Value<?> or(Value<?> left, Value<?> right) {
    return LogicUtil.binaryOperation(left, right, BooleanValue::or)
        .orElseThrow(() -> new IllegalLogicOperationException("Operands for or are not integers"));
  }

  public static Value<?> compare(Value<?> left, Value<?> right) {
    if (left instanceof NumberValue ln && right instanceof NumberValue rn) {
      return ln.compare(rn);
    }

    if (left instanceof StringValue || right instanceof StringValue) {
      return left.asStringValue().compare(right.asStringValue());
    }

    return LogicUtil.binaryOperation(left, right, BooleanValue::compare)
        .orElseThrow(
            () -> new IllegalLogicOperationException("Operands for compare are not integers"));
  }

  public static Value<?> shiftRight(Value<?> left, Value<?> right) {
    return MathUtil.binaryOperation(left, right, NumberValue::logicalShiftRight)
        .orElseThrow(
            () -> new IllegalLogicOperationException("Operands for shift right are not integers"));
  }
}
