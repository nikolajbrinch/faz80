package dk.nikolajbrinch.parser;

import java.io.File;
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

  @Override
  public void newFile(File file) throws IOException {
    scannerRegistry.register(file);
  }

  @Override
  public void newFile(String filename) throws IOException {
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
