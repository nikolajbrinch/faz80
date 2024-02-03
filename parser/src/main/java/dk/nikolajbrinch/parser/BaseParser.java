package dk.nikolajbrinch.parser;

import dk.nikolajbrinch.faz80.base.TaskResult;
import dk.nikolajbrinch.faz80.base.logging.Logger;
import dk.nikolajbrinch.faz80.base.logging.LoggerFactory;
import dk.nikolajbrinch.scanner.ScannerSource;
import dk.nikolajbrinch.scanner.SourceInfo;
import dk.nikolajbrinch.scanner.Token;
import dk.nikolajbrinch.scanner.TokenProducer;
import dk.nikolajbrinch.scanner.TokenType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseParser<S, C extends BaseParserConfiguration, E extends TokenType, T extends Token, R extends TaskResult>
    implements Parser<S, R> {

  private final Logger logger = LoggerFactory.getLogger();
  private final C configuration;
  private final TokenProducer<T> tokenProducer;
  private final List<ParseError> errors = new ArrayList<>();

  protected BaseParser(C configuration, TokenProducer<T> tokenProducer) {
    this.tokenProducer = tokenProducer;
    this.configuration = configuration;
  }

  public C getConfiguration() {
    return configuration;
  }

  public List<ParseError> getErrors() {
    return errors;
  }

  protected void newSource(ScannerSource source) throws IOException {
    tokenProducer.newSource(source);
  }

  protected void newSource(String filename) throws IOException {
    tokenProducer.newSource(filename);
  }

  protected SourceInfo getSourceInfo() {
    return tokenProducer.getSourceInfo();
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

  protected boolean match(T token, E... types) {
    for (E type : types) {
      if (isType(token, type)) {
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
   * Consumes and returns the next token
   *
   * @return
   */
  protected T nextToken() {
    if (configuration.isIgnoreComments()) {
      ignoreComments();
    }

    T token = tokenProducer.next();

    logger.debug("nextToken(%s)", token);

    return token;
  }

  /**
   * Looks at the next token without consuming it
   *
   * @return
   */
  protected T peek() {
    if (configuration.isIgnoreComments()) {
      ignoreComments();
    }

    return tokenProducer.peek();
  }

  /**
   * Look ahead at a token without consuming it
   *
   * @return
   */
  protected T peek(int position) {
    if (configuration.isIgnoreComments()) {
      ignoreComments();
    }
    return tokenProducer.peek(position);
  }

  protected T search(E... types) {
    if (configuration.isIgnoreComments()) {
      ignoreComments();
    }

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
   * Checks if the next token is EOF
   *
   * @return
   */
  protected boolean isEof() {
    return match(peek(), getEofTypes());
  }

  protected boolean isEof(int position) {
    return match(peek(position), getEofTypes());
  }

  protected boolean isNotType(T token, E type) {
    return !isType(token, type);
  }

  private void ignoreComments() {
    while (!match(tokenProducer.peek(), getEofTypes())
        && match(tokenProducer.peek(), getCommentTypes())) {
      tokenProducer.next();
    }
  }

  protected abstract boolean isType(T token, E type);

  protected abstract E[] getEofTypes();

  protected abstract E[] getCommentTypes();

  protected void reportError(ParseException e) {
    errors.add(new ParseError(e));
  }
}
