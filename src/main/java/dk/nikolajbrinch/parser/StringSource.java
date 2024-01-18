package dk.nikolajbrinch.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringSource extends BaseScannerSource<byte[]> {

  private static final MessageDigest MESSAGE_DIGEST;

  static {
    try {
      MESSAGE_DIGEST = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public StringSource(String source) {
    super(source.getBytes(StandardCharsets.UTF_8));
  }

  public StringSource(SourceInfo sourceInfo, String source) {
    super(sourceInfo, source.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public InputStream openStream() throws IOException {
    return new ByteArrayInputStream(getSource());
  }

  @Override
  protected SourceInfo createSourceInfo() throws Exception {
    return new SourceInfo(digest(getSource()));
  }

  private static synchronized String digest(byte[] source) {
    MESSAGE_DIGEST.reset();
    MESSAGE_DIGEST.update(source);
    return new String(MESSAGE_DIGEST.digest(), StandardCharsets.UTF_8);
  }
}
