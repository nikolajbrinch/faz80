package dk.nikolajbrinch.faz80.parser.base;

/** Utility class for handling identifier normalization. */
public class IdentifierNormalizer {

  private IdentifierNormalizer() {}

  /**
   * Normalizes the given identifier name by removing leading dots and trailing colons.
   *
   * @param name the identifier name to normalize
   * @return the normalized identifier name
   */
  public static String normalize(String name) {
    return name.replaceAll("^(?:[.]*)?([^.]+?)(?:\\:*)?$", "$1");
  }
}
