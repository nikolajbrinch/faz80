package dk.nikolajbrinch.faz80.scanner;

import dk.nikolajbrinch.scanner.TokenType;

public enum AssemblerTokenType implements TokenType {
  /*
   * Single character tokens
   */

  NEWLINE,
  LEFT_PAREN,
  RIGHT_PAREN,
  LEFT_BRACKET,
  RIGHT_BRACKET,
  LEFT_BRACE,
  RIGHT_BRACE,
  PLUS,
  MINUS,
  STAR,
  SLASH,
  PERCENT,
  HASH,
  COMMA,

  /*
   * One, two or three character tokens
   */
  DOLLAR,
  DOLLAR_DOLLAR,
  CARET,
  CARET_CARET,
  COLON,
  COLON_COLON,
  BANG,
  BANG_EQUAL,
  EQUAL,
  EQUAL_EQUAL,
  GREATER,
  GREATER_GREATER,
  GREATER_GREATER_GREATER,
  GREATER_EQUAL,
  LESS,
  LESS_LESS,
  LESS_EQUAL,
  AND,
  AND_AND,
  PIPE,
  PIPE_PIPE,
  TILDE,

  /*
   * Literals
   */
  IDENTIFIER,
  STRING,
  CHAR,
  DECIMAL_NUMBER,
  HEX_NUMBER,
  BINARY_NUMBER,
  OCTAL_NUMBER,
  COMMENT,

  /*
   * Keywords
   */
  ORIGIN,
  CONSTANT,
  SET,
  DEFINE,
  ASSIGN,
  INCLUDE,
  INSERT,
  ALIGN,
  MACRO,
  ENDMACRO,
  REPEAT,
  ENDREPEAT,
  DUPLICATE,
  ENDDUPLICATE,
  LOCAL,
  ENDLOCAL,
  PHASE,
  DEPHASE,
  IF,
  ELSE,
  ELSE_IF,
  ENDIF,
  ASSERT,
  GLOBAL,
  DATA_BYTE,
  DATA_WORD,
  DATA_LONG,
  DATA_TEXT,
  DATA_BLOCK,
  DATA,
  CODE_SEGMENT,
  DATA_SEGMENT,
  TARGET,
  END,
  DIRECTIVE,
  ERROR, EOF
}
