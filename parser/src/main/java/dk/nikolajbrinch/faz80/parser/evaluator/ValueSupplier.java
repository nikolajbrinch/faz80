package dk.nikolajbrinch.faz80.parser.evaluator;

import dk.nikolajbrinch.faz80.parser.values.NumberValue;
import dk.nikolajbrinch.faz80.parser.values.Value;
import java.util.function.Supplier;

public interface ValueSupplier extends Supplier<Value<?>> {

  static ValueSupplier of(Supplier<Value<?>> supplier) {
    return supplier::get;
  }

  default NumberValue number() { return get().asNumberValue(); }
}
