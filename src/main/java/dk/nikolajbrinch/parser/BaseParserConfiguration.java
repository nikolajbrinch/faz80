package dk.nikolajbrinch.parser;

public class BaseParserConfiguration {

  private final boolean debug = false;
  private final boolean ignoreComments;

  public BaseParserConfiguration() {
    this(true);
  }

  public BaseParserConfiguration(boolean ignoreComments) {
    this.ignoreComments = ignoreComments;
  }

  public boolean isIgnoreComments() {
    return ignoreComments;
  }
}
