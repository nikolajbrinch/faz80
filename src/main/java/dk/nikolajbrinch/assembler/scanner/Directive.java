package dk.nikolajbrinch.assembler.scanner;

import java.util.Arrays;

public enum Directive {
  ORIGIN(".org", "org", ".loc"),
  CONSTANT("equ"),
  ASSIGN("defl", "="),
  SET("set"),
  INCLUDE(".include", "include", "#include"),
  INSERT(".incbin", "incbin", "#insert"),
  ALIGN(".align", "align"),
  MACRO(".macro", "macro"),
  ENDMACRO(".endm", "endm"),
  REPEAT(".rept", "rept"),
  ENDREPEAT(".endr", "endr"),
  DUPLICATE(".dup", "dup"),
  ENDDUPLICATE(".edup", "edup"),
  LOCAL(".local"),
  ENDLOCAL(".endlocal"),
  PHASE(".phase"),
  DEPHASE(".dephase"),
  IF(".if", "if", "#if"),
  ELSE("#else"),
  ELSE_IF("#elif"),
  ENDIF(".endif", "endif", "#endif"),
  ASSERT(".assert", "#assert"),
  GLOBAL(".globl"),
  DATA_BYTE(".byte", "defb", "db", ".db"),
  DATA_WORD(".word", "defw", "dw", ".dw"),
  DATA_LONG("'.long"),
  DATA_TEXT(".text", "defm", "dm", ".dm", ".ascii"),
  DATA_BLOCK("defs", "ds", ".block", ".blkb"),
  DATA("data"),
  END(".end", "end", "#end");

  private final String[] keywords;

  private Directive(String... keywords) {
    this.keywords = keywords;
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
