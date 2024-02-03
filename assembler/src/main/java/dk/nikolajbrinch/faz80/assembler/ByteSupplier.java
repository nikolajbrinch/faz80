package dk.nikolajbrinch.faz80.assembler;

import java.util.function.LongSupplier;

public interface ByteSupplier extends LongSupplier {

  static ByteSupplier of(long l) {
    return () -> l & 0xFF;
  }

  static ByteSupplier of(LongSupplier supplier) {
    return supplier::getAsLong;
  }
}
