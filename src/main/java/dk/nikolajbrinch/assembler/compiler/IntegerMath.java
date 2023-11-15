package dk.nikolajbrinch.assembler.compiler;

public class IntegerMath {

  public static Object divide(Object left, Object right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.divide(r);
    }

    throw new IllegalStateException("Operands for division are not integers");
  }

  public static Object multiply(Object left, Object right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.multiply(r);
    }

    throw new IllegalStateException("Operands for multiplication are not integers");
  }

  public static Object subtract(Object left, Object right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.subtract(r);
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }

  public static Object add(Object left, Object right) {
    if (left instanceof Value l && right instanceof Value r) {
      if (l instanceof NumberValue ln && r instanceof NumberValue rn) {
        return ln.add(rn);
      }

      return l.asStringValue().add(r.asStringValue());
    }

    throw new IllegalStateException("Operand for addition is not integer, nor string");
  }

  public static Object negate(Object value) {
    if (value instanceof NumberValue v) {
      return v.negate();
    }

    throw new IllegalStateException("Operand for negation is not integer");
  }

}
