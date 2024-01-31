package dk.nikolajbrinch.assembler.util;

public class StringUtil {

  public static String unquote(String string) {
    if (string.startsWith("\"") && string.endsWith("\"")
        || string.startsWith("'") && string.endsWith("'")) {
      return string.substring(1, string.length() - 1);
    }

    return string;
  }
}
