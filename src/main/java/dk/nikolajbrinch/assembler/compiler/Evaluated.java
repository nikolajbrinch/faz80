package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.compiler.instructions.ValueSupplier;
import dk.nikolajbrinch.assembler.compiler.values.NumberValue.Size;
import dk.nikolajbrinch.assembler.compiler.values.Value;

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
