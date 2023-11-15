package dk.nikolajbrinch.assembler.parser;

import dk.nikolajbrinch.assembler.scanner.Scanner;
import dk.nikolajbrinch.assembler.scanner.Token;
import dk.nikolajbrinch.assembler.scanner.TokenType;

public class Control {

  private boolean debug = false;

  private final Scanner scanner;

  public Control(Scanner scanner) {
    this.scanner = scanner;
  }

  Token consume(TokenType type, String message) {
    if (checkType(type)) {
      return nextToken();
    }

    throw error(peek(), message);
  }

  RuntimeException error(Token token, String message) {
    reportError(token, message);

    return new ParseException(message);
  }

  static void reportError(Token token, String message) {
    if (token.type() == TokenType.EOF) {
      report(token.line() + ", " + token.start() + ": at end", message);
    } else {
      report(token.line() + ", " + token.start() + ": at '" + token.text() + "'", message);
    }
  }

  static void report(String location, String message) {
    System.out.println(message);
    System.out.println(location);
  }

  /**
   * Checks if the next token matches a set of token types without consuming the next token
   *
   * @return
   */
  boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (checkType(type)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks if the next token is of a specific type without consuming the next token
   *
   * @return
   */
  boolean checkType(TokenType type) {
    if (isEof()) {
      return false;
    }

    return peek().type() == type;
  }

  /**
   * Checks if the next token is EOF
   *
   * @return
   */
  boolean isEof() {
    return peek().type() == TokenType.EOF;
  }

  boolean isEol() {
    return isEof() || peek().type() == TokenType.NEWLINE;
  }

  boolean isEof(int position) {
    return peek(position).type() == TokenType.EOF;
  }

  boolean isEol(int position) {
    return isEof(position) || peek(position).type() == TokenType.NEWLINE;
  }

  /**
   * Looks at the next token without consuming it
   *
   * @return
   */
  Token peek() {
    ignoreComments();

    return scanner.peek();
  }

  /**
   * Look ahead at a token without consuming it
   *
   * @return
   */
  Token peek(int position) {
    ignoreComments();

    return scanner.peek(position);
  }

  public boolean lineHasToken(TokenType type) {
    boolean hasToken = false;

    int position = 1;

    while (!(peek(position).type() == TokenType.NEWLINE || peek(position).type() == TokenType.EOF)
        && !hasToken) {
      hasToken = (peek(position).type() == type);

      position++;
    }

    return hasToken;
  }

  public Token search(TokenType... types) {
    ignoreComments();

    int position = 1;

    while (true) {
      if (isEof(position)) {
        break;
      }

      Token token = peek(position);

      for (TokenType type : types) {
        if (token.type() == type) {
          return token;
        }
      }

      position++;
    }

    return null;
  }

  /**
   * Consumes and returns the next token
   *
   * @return
   */
  Token nextToken() {
    ignoreComments();

    Token token = scanner.next();

    if (debug) {
      System.out.println("nextToken(" + token + ")");
    }

    return token;
  }

  void consumeBlankLines() {
    while (match(TokenType.NEWLINE)) {
      nextToken();
    }
  }

  private void ignoreComments() {
    while (scanner.peek().type() != TokenType.EOF && scanner.peek().type() == TokenType.COMMENT) {
      scanner.next();
    }
  }
}
