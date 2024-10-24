package dk.nikolajbrinch.scanner.impl;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

public class DefaultReplacementCharReader extends ReplacementCharReaderImpl {

  private static final Set<Marker> MARKERS =
      Set.of(new Marker("${", "}"), new Marker("&"), new Marker("$"), new Marker("\\"));

  public DefaultReplacementCharReader(InputStream inputStream, Map<String, Object> symbols) {
    this(inputStream, UTF_8, symbols);
  }

  public DefaultReplacementCharReader(String source, Map<String, Object> symbols) {
    this(source, UTF_8, symbols);
  }

  public DefaultReplacementCharReader(String source, Charset charset, Map<String, Object> symbols) {
    this(new ByteArrayInputStream(source.getBytes(charset)), charset, symbols);
  }

  public DefaultReplacementCharReader(
      InputStream inputStream, Charset charset, Map<String, Object> symbols) {
    super(inputStream, charset, MARKERS, symbols);
  }
}
