package dk.nikolajbrinch.parser;

public abstract class BaseParser<E extends TokenType, T extends Token> implements Parser {

  private final Scanner<T> scanner;
  private final boolean debug = false;

  protected BaseParser(Scanner<T> scanner) {
    this.scanner = scanner;
  }

  /**
   * Checks if the next token matches a set of token types without consuming the next token
   *
   * @return
   */
  protected boolean match(E... types) {
    for (E type : types) {
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
  protected boolean checkType(E type) {
    if (isEof()) {
      return false;
    }

    return isType(peek(), type);
  }

  /**
   * Looks at the next token without consuming it
   *
   * @return
   */
  protected T peek() {
    ignoreComments();

    return scanner.peek();
  }

  /**
   * Look ahead at a token without consuming it
   *
   * @return
   */
  protected T peek(int position) {
    ignoreComments();

    return scanner.peek(position);
  }

  protected T search(E... types) {
    ignoreComments();

    int position = 1;

    while (true) {
      if (isEof(position)) {
        break;
      }

      T token = peek(position);

      for (E type : types) {
        if (isType(token, type)) {
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
  protected T nextToken() {
    ignoreComments();

    T token = scanner.next();

    if (debug) {
      System.out.println("nextToken(" + token + ")");
    }

    return token;
  }

  /**
   * Checks if the next token is EOF
   *
   * @return
   */
  protected boolean isEof() {
    return isType(peek(), getEofType());
  }

  protected boolean isEof(int position) {
    return isType(peek(position), getEofType());
  }

  protected boolean isNotType(T token, E type) {
    return !isType(token, type);
  }

  private void ignoreComments() {
    while (isNotType(scanner.peek(), getEofType()) && isType(scanner.peek(), getCommentType())) {
      scanner.next();
    }
  }

  protected abstract boolean isType(T token, E type);

  protected abstract E getEofType();

  protected abstract E getCommentType();
}
