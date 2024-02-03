package dk.nikolajbrinch.faz80.parser.evaluator;

import dk.nikolajbrinch.faz80.parser.values.NumberValue.Size;
import dk.nikolajbrinch.faz80.parser.values.Value;

public record Evaluated(Value<?> value, ValueSupplier valueSupplier, Size size, boolean isLazy) {

  static Evaluated of(Value<?> value, Size size) {
    return new Evaluated(value, null, size, false);
  }

  static Evaluated of(ValueSupplier valueSupplier, Size size) {
    return new Evaluated(null, valueSupplier, size, true);
  }

  public Value<?> val() {
    if (isLazy) {
      return valueSupplier.get();
    }

    return value;
  }
}
