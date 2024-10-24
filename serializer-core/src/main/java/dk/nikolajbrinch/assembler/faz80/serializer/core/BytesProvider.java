package dk.nikolajbrinch.assembler.faz80.serializer.core;

public interface BytesProvider {

  boolean hasBytes();

  byte[] next();
}
