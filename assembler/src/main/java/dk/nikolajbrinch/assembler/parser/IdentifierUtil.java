package dk.nikolajbrinch.assembler.parser;

public class IdentifierUtil {
  public static String normalize(String name) {
    return name.replaceAll("^(?:[.]*?)([^.]+?)(?:\\:*?)$", "$1");
  }

}
