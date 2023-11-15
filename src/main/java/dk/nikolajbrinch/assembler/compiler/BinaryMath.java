package dk.nikolajbrinch.assembler.compiler;

public class BinaryMath {

  public static Object and(Object left, Object right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.bitwiseAnd(r);
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }

  public static Object or(Object left, Object right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.bitwiseOr(r);
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }

  public static Object xor(Object left, Object right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.bitwiseXor(r);
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }

  public static Object shiftRight(Object left, Object right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.bitwiseShiftRigth(r);
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }

  public static Object shiftLeft(Object left, Object right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.bitwiseShiftLeft(r);
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }

  public static Object not(Object value) {
    if (value instanceof NumberValue v) {
      return v.bitwiseNot();
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }
}
