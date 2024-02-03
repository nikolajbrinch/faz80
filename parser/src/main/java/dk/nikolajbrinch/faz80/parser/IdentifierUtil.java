package dk.nikolajbrinch.faz80.parser;

public class IdentifierUtil {
  public static String normalize(String name) {
    return name.replaceAll("^(?:[.]*?)([^.]+?)(?:\\:*?)$", "$1");
  }

}
