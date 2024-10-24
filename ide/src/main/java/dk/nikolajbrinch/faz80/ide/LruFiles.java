package dk.nikolajbrinch.faz80.ide;

import dk.nikolajbrinch.faz80.ide.util.LruList;
import java.nio.file.Path;

public class LruFiles {

  private final LruList<Path> lruCache;

  public LruFiles(int maxSize) {
    this.lruCache = new LruList<>(maxSize);
  }

  public void addFile(Path filePath) {
    lruCache.add(filePath);
  }

  public void removeFile(Path filePath) {
    lruCache.remove(filePath);
  }

  public void clear() {
    lruCache.clear();
  }

  public Path[] getRecentFiles() {
    return lruCache.toArray(new Path[0]);
  }
}
