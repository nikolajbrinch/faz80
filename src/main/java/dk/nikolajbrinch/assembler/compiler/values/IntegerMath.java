package dk.nikolajbrinch.assembler.compiler.values;

public final class IntegerMath {

  public static Value<?> divide(Value<?> left, Value<?> right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.divide(r);
    }

    throw new IllegalMathOperationException("Operands for division are not integers");
  }

  public static Value<?> multiply(Value<?> left, Value<?> right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.multiply(r);
    }

    throw new IllegalMathOperationException("Operands for multiplication are not integers");
  }

  public static Value<?> subtract(Value<?> left, Value<?> right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.subtract(r);
    }

    throw new IllegalMathOperationException("Operands for subtraction are not integers");
  }

  public static Value<?> add(Value<?> left, Value<?> right) {
    if (left instanceof Value l && right instanceof Value r) {
      if (l instanceof NumberValue ln && r instanceof NumberValue rn) {
        return ln.add(rn);
      }

      return l.asStringValue().add(r.asStringValue());
    }

    throw new IllegalMathOperationException("Operand for addition is not integer, nor string");
  }

  public static Value<?> negate(Value<?> value) {
    if (value instanceof NumberValue v) {
      return v.negate();
    }

    throw new IllegalMathOperationException("Operand for negation is not integer");
  }
}
