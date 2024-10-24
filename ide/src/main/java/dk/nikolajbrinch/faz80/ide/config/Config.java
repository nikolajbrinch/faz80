package dk.nikolajbrinch.faz80.ide.config;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public enum Config {
  INSTANCE;

  private ConfigValues values;

  public void load() {
    ConfigSerializer configSerializer = new ConfigSerializer();
    ConfigValues values = null;

    try {
      values = configSerializer.load();
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }

    this.values = values;
  }

  public void save() {
    ConfigSerializer configSerializer = new ConfigSerializer();

    try {
      configSerializer.save(this.values);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Path> getLruFiles() {
    List<String> files = values.getLruFiles();

    List<Path> lruFiles = new ArrayList<>();

    if (files != null) {
      lruFiles = files.stream().map(Paths::get).toList();
    }

    return lruFiles;
  }

  public Config setLruFiles(List<Path> files) {
    values.setLruFiles(files.stream().map(Path::toString).toList());

    return this;
  }

  public WindowDimensions getWindowDimensions() {
    return values.getWindowDimensions();
  }

  public Config setWindowDimensions(WindowDimensions windowDimensions) {
    values.setWindowDimensions(windowDimensions);

    return this;
  }

  public Path getWorkingDirectory() {
    return values.getWorkingDirectory() == null ? null : Paths.get(values.getWorkingDirectory());
  }

  public Config setWorkingDirectory(Path path) {
    values.setWorkingDirectory(path.toString());

    return this;
  }
}
