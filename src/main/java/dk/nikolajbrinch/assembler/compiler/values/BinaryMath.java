package dk.nikolajbrinch.assembler.compiler.values;

public final class BinaryMath {

  public static Value<?> and(Value<?> left, Value<?> right) {
    return MathUtil.binaryOperation(left, right, NumberValue::bitwiseAnd)
        .orElseThrow(() -> new IllegalMathOperationException("Operands for and are not integers"));
  }

  public static Value<?> or(Value<?> left, Value<?> right) {
    return MathUtil.binaryOperation(left, right, NumberValue::bitwiseOr)
        .orElseThrow(() -> new IllegalMathOperationException("Operands for or are not integers"));
  }

  public static Value<?> xor(Value<?> left, Value<?> right) {
    return MathUtil.binaryOperation(left, right, NumberValue::bitwiseXor)
        .orElseThrow(() -> new IllegalMathOperationException("Operands for xor are not integers"));
  }

  public static Value<?> shiftRight(Value<?> left, Value<?> right) {
    return MathUtil.binaryOperation(left, right, NumberValue::bitwiseShiftRigth)
        .orElseThrow(() -> new IllegalMathOperationException("Operands for shift right are not integers"));
  }

  public static Value<?> shiftLeft(Value<?> left, Value<?> right) {
    return MathUtil.binaryOperation(left, right, NumberValue::bitwiseShiftLeft)
        .orElseThrow(() -> new IllegalMathOperationException("Operands for shift left are not integers"));
  }

  public static Value<?> not(Value<?> value) {
    return MathUtil.unaryOperation(value, NumberValue::bitwiseNot)
        .orElseThrow(() -> new IllegalMathOperationException("Operands for not are not integers"));
  }
}
