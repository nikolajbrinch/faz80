package dk.nikolajbrinch.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class UrlSource extends BaseScannerSource<URL> {

  public UrlSource(URL url) {
    super(url);
  }

  public UrlSource(String url) {
    this(toUrl(url));
  }

  public UrlSource(SourceInfo sourceInfo, URL url) {
    super(sourceInfo, url);
  }

  public UrlSource(SourceInfo sourceInfo, String url) {
    this(sourceInfo, toUrl(url));
  }

  @Override
  public InputStream openStream() throws IOException {
    return getSource().openStream();
  }

  @Override
  protected SourceInfo createSourceInfo() throws Exception {
    return new SourceInfo(getSource().toExternalForm());
  }

  private static URL toUrl(String url) {
    try {
      return URI.create(url).toURL();
    } catch (MalformedURLException e) {
      throw new UncheckedIOException(e);
    }
  }
}
