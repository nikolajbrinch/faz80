package dk.nikolajbrinch.faz80.base.util;

public class StringUtil {

  public static String unquote(String string) {
    if (string.startsWith("\"") && string.endsWith("\"")
        || string.startsWith("'") && string.endsWith("'")) {
      return string.substring(1, string.length() - 1);
    }

    return string;
  }

  public static String escape(String value) {
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);

      builder.append(
          switch (ch) {
            case '\0' -> "\\0";
            case '\n' -> "\\n";
            case '\r' -> "\\r";
            case '\t' -> "\\t";
            case '\b' -> "\\b";
            case '\f' -> "\\f";
            case '\\' -> "\\";
            default -> ch;
          });
    }

    return builder.toString();
  }

}
