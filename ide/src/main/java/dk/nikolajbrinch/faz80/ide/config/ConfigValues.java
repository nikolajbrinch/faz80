package dk.nikolajbrinch.faz80.ide.config;

import java.util.List;

public class ConfigValues {

  private List<String> lruFiles;

  private List<String> openFiles;

  private String workingDirectory;

  private WindowDimensions windowDimensions;

  public WindowDimensions getWindowDimensions() {
    return windowDimensions;
  }

  public void setWindowDimensions(WindowDimensions windowDimensions) {
    this.windowDimensions = windowDimensions;
  }

  public List<String> getLruFiles() {
    return lruFiles;
  }

  public void setLruFiles(List<String> files) {
    this.lruFiles = files;
  }

  public String getWorkingDirectory() {
    return workingDirectory;
  }

  public void setWorkingDirectory(String workingDirectory) {
    this.workingDirectory = workingDirectory;
  }
}
