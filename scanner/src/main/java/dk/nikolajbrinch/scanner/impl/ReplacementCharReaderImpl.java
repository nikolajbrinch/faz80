package dk.nikolajbrinch.scanner.impl;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The ReplacementCharReaderImpl class extends BufferedCharReaderImpl and is designed to read
 * characters from an input stream or string while replacing certain substrings (symbols) with their
 * corresponding values. It uses markers to identify the start and end of symbols that need to be
 * replaced.
 */
public class ReplacementCharReaderImpl extends BufferedCharReaderImpl {

  private final Set<Marker> markers;
  private final Map<String, Object> symbols;

  private final Map<String, String> symbolTable = new HashMap<>();
  private List<String> subst;

  public ReplacementCharReaderImpl(
      InputStream inputStream, Set<Marker> markers, Map<String, Object> symbols) {
    this(inputStream, UTF_8, markers, symbols);
  }

  public ReplacementCharReaderImpl(
      String source, Set<Marker> markers, Map<String, Object> symbols) {
    this(source, UTF_8, markers, symbols);
  }

  public ReplacementCharReaderImpl(
      String source, Charset charset, Set<Marker> markers, Map<String, Object> symbols) {
    this(new ByteArrayInputStream(source.getBytes(charset)), charset, markers, symbols);
  }

  public ReplacementCharReaderImpl(
      InputStream inputStream, Charset charset, Set<Marker> markers, Map<String, Object> symbols) {
    super(inputStream, charset);
    this.markers = markers;
    this.symbols = symbols;
    buildSymbolTable();
  }

  @Override
  protected String readLine(Reader reader) throws IOException {
    return replace(super.readLine(reader));
  }

  private String replace(String line) {
    if (line == null) {
      return null;
    }

    StringBuilder builder = new StringBuilder();

    int i = 0;

    while (i < line.length()) {
      boolean found = false;

      if (checkMarkers(line.charAt(i))) {
        for (String sub : subst) {
          if (i + sub.length() < line.length()) {
            String test = line.substring(i, i + sub.length());
            if (test.equals(sub)) {
              found = true;
              builder.append(symbols.get(symbolTable.get(sub)));
              i += sub.length();
              break;
            }
          }
        }
      }

      if (!found) {
        builder.append(line.charAt(i));
        i++;
      }
    }

    return builder.toString();
  }

  private boolean checkMarkers(char ch) {
    for (Marker marker : markers) {
      if (marker.startsWith(ch)) {
        return true;
      }
    }

    return false;
  }

  private void buildSymbolTable() {
    for (Marker marker : markers) {
      for (String symbolName : symbols.keySet()) {
        StringBuilder builder = new StringBuilder().append(marker.start()).append(symbolName);
        if (marker.end() != null) {
          builder.append(marker.end());
        }

        symbolTable.put(builder.toString(), symbolName);
      }
    }

    subst =
        symbolTable.keySet().stream()
            .sorted(Comparator.comparingInt(String::length).reversed())
            .toList();
  }
}
