package dk.nikolajbrinch.macro.scanner;

import java.util.Arrays;

public enum Directive {
  INCLUDE(".include", "include", "#include"),
  MACRO(".macro", "macro"),
  ENDMACRO(".endm", "endm");
  private final String[] keywords;

  Directive(String... keywords) {
    this.keywords = keywords;
  }

  public String[] getKeywords() {
    return keywords;
  }

  public static Directive find(String text) {
    return Arrays.stream(values())
        .filter(directive -> matchKeyword(text, directive))
        .findAny()
        .orElse(null);
  }

  private static boolean matchKeyword(String text, Directive directive) {
    for (String keyword : directive.keywords) {
      if (keyword.equals(text)) {
        return true;
      }
    }

    return false;
  }
}
