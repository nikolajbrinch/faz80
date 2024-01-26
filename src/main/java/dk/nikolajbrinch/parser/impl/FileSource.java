package dk.nikolajbrinch.parser.impl;

import dk.nikolajbrinch.parser.BaseScannerSource;
import dk.nikolajbrinch.parser.SourceInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileSource extends BaseScannerSource<File> {

  public FileSource(File file) {
    super(file);
  }

  public FileSource(String filename) {
    this(new File(filename));
  }

  public FileSource(SourceInfo sourceInfo, File file) {
    super(sourceInfo, file);
  }

  public FileSource(SourceInfo sourceInfo, String filename) {
    this(sourceInfo, new File(filename));
  }

  @Override
  public InputStream openStream() throws IOException {
    return new FileInputStream(getSource());
  }

  @Override
  protected SourceInfo createSourceInfo() throws Exception {
    return new SourceInfo(getSource().getCanonicalPath());
  }
}
