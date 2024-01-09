package dk.nikolajbrinch.macro.scanner;

import dk.nikolajbrinch.parser.TokenType;

public enum MacroTokenType implements TokenType {
  /*
   * Single character tokens
   */

  NEWLINE,
  MACRO,
  ENDMACRO,
  LEFT_PAREN,
  RIGHT_PAREN,
  LEFT_BRACKET,
  RIGHT_BRACKET,
  LEFT_BRACE,
  RIGHT_BRACE,
  GREATER,
  LESS,
  COMMA,
  IDENTIFIER,
  TEXT,
  COMMENT,
  EQUAL,
  STRING,
  INCLUDE,
  EOF
}
