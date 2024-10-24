package dk.nikolajbrinch.assembler.faz80.serializer.core;

import java.io.IOException;
import java.io.OutputStream;

public interface Serializer {

  void serialize(OutputStream outputStream, BytesProvider bytesProvider) throws IOException;

}
