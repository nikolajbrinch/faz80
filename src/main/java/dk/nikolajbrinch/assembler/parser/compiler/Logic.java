package dk.nikolajbrinch.assembler.parser.compiler;

public class Logic {

  public static Object not(Object value) {
    if (value instanceof Boolean v) {
      return !v;
    }

    if (value instanceof Value v) {
      return !v.asBoolean();
    }

    throw new IllegalStateException("Operands for subtraction are not integers");
  }

  public static Object and(Object left, Object right) {
    if (left instanceof Boolean l && right instanceof Boolean r) {
      return l && r;
    }

    if (left instanceof Value l && right instanceof Value r) {
      return l.asBoolean() && r.asBoolean();
    }

    if (left instanceof Boolean l && right instanceof Value r) {
      return l && r.asBoolean();
    }

    if (left instanceof Value l && right instanceof Boolean r) {
      return l.asBoolean() && r;
    }

    throw new IllegalStateException("Operands for subtraction are not booleans");

  }
  public static Object or(Object left, Object right) {
    if (left instanceof Boolean l && right instanceof Boolean r) {
      return l || r;
    }

    if (left instanceof Value l && right instanceof Value r) {
      return l.asBoolean() || r.asBoolean();
    }

    if (left instanceof Boolean l && right instanceof Value r) {
      return l || r.asBoolean();
    }

    if (left instanceof Value l && right instanceof Boolean r) {
      return l.asBoolean() || r;
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

    if (left instanceof Boolean l && right instanceof Boolean r) {
      return l.equals(r);
    }

    if (left instanceof Boolean l && right instanceof Value r) {
      return l.equals(r.asBoolean());
    }

    if (left instanceof Value l && right instanceof Boolean r) {
      return l.asBoolean().equals(r);
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
