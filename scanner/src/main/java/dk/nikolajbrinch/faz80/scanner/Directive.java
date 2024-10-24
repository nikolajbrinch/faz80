package dk.nikolajbrinch.faz80.scanner;

import java.util.Arrays;

/** Represents a directive in the assembler */
public enum Directive {
  ORIGIN(".org", "org", ".loc"),
  MODULE(".module", "module"),
  PUBLIC(".public", "public"),
  EXTERN(".extern", "extern"),
  SECTION(".section", "section"),
  CONSTANT("equ"),
  ASSIGN("defl", "=", "defc", "dc", ".dc"),
  VARS("defvars", ".defvars"),
  GROUP("defgroup", ".defgroup"),
  SET(".set", "set"),
  INCLUDE(".include", "include", "#include"),
  INSERT("binary", ".incbin", "incbin", "#insert"),
  ALIGN(".align", "align"),
  MACRO(".macro", "macro"),
  EXITMACRO(".exitm", "exitm"),
  ENDMACRO(".endm", "endm"),
  REPEAT(".rept", "rept"),
  REPEAT_CHAR(".reptc", "reptc"),
  REPEAT_EXPR(".repti", "repti"),
  ENDREPEAT(".endr", "endr"),
  DUPLICATE(".dup", "dup"),
  ENDDUPLICATE(".edup", "edup"),
  LOCAL("#local", ".local", "local"),
  ENDLOCAL("#endlocal", ".endlocal"),
  PHASE(".phase", "phase"),
  DEPHASE(".dephase", "dephase"),
  IF(".if", "if", "#if"),
  IF_DEFINED(".ifdef", "ifdef", "#ifdef"),
  IF_NOT_DEFINED(".ifndef", "ifndef", "#ifndef"),
  ELSE(".else", "else", "#else"),
  ELSE_IF(".elif", "elif", "#elif"),
  ELSE_IF_DEFINED(".elifdef", "elifdef", "#elifdef"),
  ELSE_IF_NOT_DEFINED(".elifndef", "elifndef", "#elifndef"),
  ENDIF(".endif", "endif", "#endif"),
  DEFINE(".define", "define", "#define"),
  UNDEFINE(".undef", "undef", "#undef", ".undefine", "undefine", "#undefine"),
  ASSERT(".assert", "#assert", "assert"),
  GLOBAL(".globl", "global"),
  ASMPC(".asmpc", "asmpc"),
  DATA_BYTE("byte", ".byte", "defb", "db", ".db"),
  DATA_WORD_LE("word", ".word", "defw", "dw", ".dw"),
  DATA_WORD_BE("defdb", "ddb", ".ddb"),
  DATA_PTR("defp", "ptr", "dp", ".dp"),
  DATA_LONG("long", ".long", "defq", "dword", ".dword", "dq", ".dq"),
  DATA_TEXT(".text", "defm", "dm", ".dm", ".ascii"),
  DATA_BLOCK("defs", "ds", ".block", ".blkb"),
  DATA("data"),
  AREA(".area"),
  TARGET("#target"),
  CODE_SEGMENT("#code"),
  DATA_SEGMENT("#data"),
  END(".end", "end", "#end");

  private final String[] keywords;

  Directive(String... keywords) {
    this.keywords = keywords;
  }

  public static Directive find(String text) {
    return Arrays.stream(values())
        .filter(directive -> matchKeyword(text, directive))
        .findAny()
        .orElse(null);
  }

  public static boolean matchKeyword(String text, Directive directive) {
    return directive.matchKeyword(text);
  }

  public boolean matchKeyword(String text) {
    for (String keyword : keywords) {
      if (keyword.equalsIgnoreCase(text)) {
        return true;
      }
    }

    return false;
  }

  public String[] getKeywords() {
    return keywords;
  }
}
