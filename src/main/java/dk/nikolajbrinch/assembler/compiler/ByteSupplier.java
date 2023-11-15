package dk.nikolajbrinch.assembler.compiler;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

public interface ByteSupplier extends LongSupplier {

  static ByteSupplier of(long l) {
    return () -> l & 0xFF;
  }
}
