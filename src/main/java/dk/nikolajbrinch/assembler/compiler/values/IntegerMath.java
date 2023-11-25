package dk.nikolajbrinch.assembler.compiler.values;

import dk.nikolajbrinch.assembler.compiler.IllegalMathOperationException;

public final class IntegerMath {

  public static Object divide(Object left, Object right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.divide(r);
    }

    throw new IllegalMathOperationException("Operands for division are not integers");
  }

  public static Object multiply(Object left, Object right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.multiply(r);
    }

    throw new IllegalMathOperationException("Operands for multiplication are not integers");
  }

  public static Object subtract(Object left, Object right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.subtract(r);
    }

    throw new IllegalMathOperationException("Operands for subtraction are not integers");
  }

  public static Object add(Object left, Object right) {
    if (left instanceof Value l && right instanceof Value r) {
      if (l instanceof NumberValue ln && r instanceof NumberValue rn) {
        return ln.add(rn);
      }

      return l.asStringValue().add(r.asStringValue());
    }

    throw new IllegalMathOperationException("Operand for addition is not integer, nor string");
  }

  public static Object negate(Object value) {
    if (value instanceof NumberValue v) {
      return v.negate();
    }

    throw new IllegalMathOperationException("Operand for negation is not integer");
  }
}
