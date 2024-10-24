package dk.nikolajbrinch.assembler.faz80.serializer.hex;

import dk.nikolajbrinch.assembler.faz80.serializer.core.AbstractSerializer;
import dk.nikolajbrinch.assembler.faz80.serializer.core.BytesProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class IntelHexSerializer extends AbstractSerializer {

  @Override
  public void serialize(OutputStream outputStream, BytesProvider bytesProvider) throws IOException {
    while (bytesProvider.hasBytes()) {
      byte[] bytes = bytesProvider.next();

      outputBytes(outputStream, bytes);
    }
  }

  private void outputBytes(OutputStream outputStream, byte[] bytes) throws IOException {
    String record = generateRecord(bytes);

    outputStream.write(record.getBytes(StandardCharsets.UTF_8));
  }

  private String generateRecord(byte[] bytes) {
    StringBuilder record = new StringBuilder();

    record.append(":");
    record.append(String.format("%02X", bytes.length));
    record.append("00");
    record.append(String.format("%02X", bytes[0]));
    record.append(String.format("%02X", bytes[1]));
    record.append(String.format("%02X", bytes[2]));
    record.append(String.format("%02X", bytes[3]));

    int checksum = bytes.length + bytes[0] + bytes[1] + bytes[2] + bytes[3];
    record.append(String.format("%02X", checksum));

    record.append("\n");

    return record.toString();
  }
}
