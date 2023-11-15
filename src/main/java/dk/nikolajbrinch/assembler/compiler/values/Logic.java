package dk.nikolajbrinch.assembler.compiler.values;


public final class Logic {

  public static Object not(Object value) {
    if (value instanceof Boolean v) {
      return !v;
    }

    if (value instanceof Value v) {
      return v.asBooleanValue().not();
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }

  public static Object and(Object left, Object right) {
    if (left instanceof BooleanValue l && right instanceof BooleanValue r) {
      return l.and(r);
    }

    if (left instanceof Value l && right instanceof Value r) {
      return l.asBooleanValue().and(r.asBooleanValue());
    }

    if (left instanceof BooleanValue l && right instanceof Value r) {
      return l.and(r.asBooleanValue());
    }

    if (left instanceof Value l && right instanceof BooleanValue r) {
      return l.asBooleanValue().and(r);
    }

    throw new IllegalStateException("Operands for subtraction are not booleans");
  }

  public static Object or(Object left, Object right) {
    if (left instanceof BooleanValue l && right instanceof BooleanValue r) {
      return l.or(r);
    }

    if (left instanceof Value l && right instanceof Value r) {
      return l.asBooleanValue().or(r.asBooleanValue());
    }

    if (left instanceof BooleanValue l && right instanceof Value r) {
      return l.or(r.asBooleanValue());
    }

    if (left instanceof Value l && right instanceof BooleanValue r) {
      return l.asBooleanValue().or(r);
    }

    throw new IllegalStateException("Operands for subtraction are not booleans");
  }

  public static Object compare(Object left, Object right) {
    if (left instanceof Value l && right instanceof Value r) {
      if (l instanceof NumberValue ln && r instanceof NumberValue rn) {
        return ln.compare(rn);
      }

      return l.asStringValue().compare(r.asStringValue());
    }

    if (left instanceof BooleanValue l && right instanceof BooleanValue r) {
      return l.compare(r);
    }

    if (left instanceof BooleanValue l && right instanceof Value r) {
      return l.compare(r.asBooleanValue());
    }

    if (left instanceof Value l && right instanceof BooleanValue r) {
      return l.asBooleanValue().compare(r);
    }

    throw new IllegalStateException("Operands for division are not integers");
  }

  public static Object shiftRight(Object left, Object right) {
    if (left instanceof NumberValue l && right instanceof NumberValue r) {
      return l.logicalShiftRight(r);
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }
}
