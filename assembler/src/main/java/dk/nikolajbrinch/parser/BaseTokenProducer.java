package dk.nikolajbrinch.parser;

import java.io.IOException;
import java.util.Optional;

public abstract class BaseTokenProducer<T> implements TokenProducer<T> {

  private final ScannerRegistry<T> scannerRegistry;

  protected BaseTokenProducer(ScannerRegistry<T> scannerRegistry) {
    this.scannerRegistry = scannerRegistry;
  }

  @Override
  public T peek() {
    return determineToken(scannerRegistry.getCurrentScanner().peek())
        .orElseGet(() -> scannerRegistry.getCurrentScanner().peek());
  }

  @Override
  public T peek(int position) {
    return determineToken(scannerRegistry.getCurrentScanner().peek(position))
        .orElseGet(() -> scannerRegistry.getCurrentScanner().peek(position));
  }

  @Override
  public T next() {
    return determineToken(scannerRegistry.getCurrentScanner().next())
        .orElseGet(() -> scannerRegistry.getCurrentScanner().next());
  }

  public SourceInfo getSourceInfo() {
    return null;
  }

  @Override
  public void newSource(ScannerSource source) throws IOException {
    scannerRegistry.register(source);
  }

  @Override
  public void newSource(String filename) throws IOException {
    scannerRegistry.register(filename);
  }

  private Optional<T> determineToken(T token) {
    if (scannerRegistry.isBaseScanner()) {
      return Optional.of(token);
    }

    if (!isEof(token)) {
      return Optional.of(token);
    }

    scannerRegistry.unregister();

    return Optional.empty();
  }

  protected abstract boolean isEof(T token);
}
