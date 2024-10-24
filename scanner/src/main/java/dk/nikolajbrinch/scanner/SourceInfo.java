package dk.nikolajbrinch.scanner;

public record SourceInfo(String name) {

  public static final SourceInfo NONE = new SourceInfo(null);
}
