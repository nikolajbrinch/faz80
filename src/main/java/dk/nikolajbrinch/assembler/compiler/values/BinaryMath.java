package dk.nikolajbrinch.assembler.compiler.values;

public final class BinaryMath {

  public static Value<?> and(Value<?> left, Value<?> right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.bitwiseAnd(r);
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }

  public static Value<?> or(Value<?> left, Value<?> right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.bitwiseOr(r);
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }

  public static Value<?> xor(Value<?> left, Value<?> right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.bitwiseXor(r);
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }

  public static Value<?> shiftRight(Value<?> left, Value<?> right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.bitwiseShiftRigth(r);
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }

  public static Value<?> shiftLeft(Value<?> left, Value<?> right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.bitwiseShiftLeft(r);
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }

  public static Value<?> not(Value<?> value) {
    if (value instanceof NumberValue v) {
      return v.bitwiseNot();
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }
}
