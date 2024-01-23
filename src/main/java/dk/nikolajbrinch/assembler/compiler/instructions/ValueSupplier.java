package dk.nikolajbrinch.assembler.compiler.instructions;

import dk.nikolajbrinch.assembler.compiler.values.NumberValue;
import dk.nikolajbrinch.assembler.compiler.values.Value;
import java.util.function.Supplier;

public interface ValueSupplier extends Supplier<Value<?>> {

  static ValueSupplier of(Supplier<Value<?>> supplier) {
    return supplier::get;
  }

  default NumberValue number() { return get().asNumberValue(); }
}
