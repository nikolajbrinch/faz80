package dk.nikolajbrinch.scanner;

import dk.nikolajbrinch.faz80.base.logging.Logger;
import dk.nikolajbrinch.faz80.base.logging.LoggerFactory;
import dk.nikolajbrinch.scanner.impl.FileSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseScannerRegistry<T> implements ScannerRegistry<T> {

  private final Logger logger = LoggerFactory.getLogger();

  private final Deque<Scanner<T>> scanners = new ArrayDeque<>();
  private final Deque<File> directories = new ArrayDeque<>();
  private final Map<String, Scanner<T>> registry = new HashMap<>();
  private final Map<Scanner<T>, String> registryReverse = new HashMap<>();
  private Scanner<T> currentScanner;
  private File currentDirectory;

  public BaseScannerRegistry(File currentDirectory) {
    this.currentDirectory = currentDirectory;
  }

  public void register(ScannerSource source) throws IOException {
    if (registry.containsKey(source.getSourceInfo().name())) {
      cyclicDependencyError(source.getSourceInfo().name());
    }

    Scanner<T> scanner = createScanner(source);
    File canonicalDirectory = directory(source);

    scanners.addFirst(scanner);
    directories.addFirst(canonicalDirectory);
    registry.put(source.getSourceInfo().name(), scanner);
    registryReverse.put(scanner, source.getSourceInfo().name());
    currentScanner = scanner;
    currentDirectory = canonicalDirectory;
  }

  private File directory(ScannerSource source) throws IOException {
    File directory = null;

    File file = new File(source.getSourceInfo().name());

    if (file.exists()) {
      directory = file.getParentFile().getCanonicalFile();
    } else {
      directory =
          currentDirectory == null ? new File(new File(".").getCanonicalPath()) : currentDirectory;
    }

    return directory;
  }

  public void register(String filename) throws IOException {
    File file = new File(filename);

    if (!file.exists()) {
      file = new File(currentDirectory, filename);
    }

    register(new FileSource(file));
  }

  public void unregister() {
    if (scanners.size() > 1) {
      Scanner<T> scanner = scanners.removeFirst();
      directories.removeFirst();
      registry.remove(registryReverse.remove(scanner));
      currentScanner = scanners.getFirst();
      currentDirectory = directories.getFirst();
    }
  }

  public Scanner<T> getCurrentScanner() {
    return currentScanner;
  }

  public boolean isBaseScanner() {
    return scanners.size() == 1;
  }

  protected abstract Scanner<T> createScanner(ScannerSource source) throws IOException;

  private void cyclicDependencyError(String includePath) {
    String includingFile = registryReverse.get(scanners.getFirst());
    logger.error("Cyclic dependency detected in: %s including: %s", includingFile, includePath);

    List<String> files = scanners.stream().map(registryReverse::get).toList().reversed();

    String indent = "  ";
    logger.error("File hierarchy:%n%s%s", indent, files.get(0));
    for (int i = 1; i < files.size(); i++) {
      logger.error("%s--> %s", indent.repeat(i + 1), files.get(i));
    }
    logger.error("%s--> %s", indent.repeat(files.size() + 1), includePath);

    throw new IllegalStateException(
        "Cyclic dependency detected in: " + includingFile + " including: " + includePath);
  }
}
