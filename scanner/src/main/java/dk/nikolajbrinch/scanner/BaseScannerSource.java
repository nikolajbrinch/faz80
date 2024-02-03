package dk.nikolajbrinch.scanner;

public abstract class BaseScannerSource<T> implements ScannerSource {

  private final SourceInfo sourceInfo;

  private final T source;

  public BaseScannerSource(T source) {
    this.source = source;
    try {
      this.sourceInfo = createSourceInfo();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public BaseScannerSource(SourceInfo sourceInfo, T source) {
    this.sourceInfo = sourceInfo;
    this.source = source;
  }

  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  public T getSource() {
    return source;
  }

  protected abstract SourceInfo createSourceInfo() throws Exception;
}
