package dk.nikolajbrinch.parser;

import dk.nikolajbrinch.faz80.parser.base.BaseException;
import dk.nikolajbrinch.faz80.scanner.AssemblerToken;

public class ParseException extends BaseException {

  private final AssemblerToken token;

  public ParseException(AssemblerToken token, String message) {
    super(message);
    this.token = token;
  }

  public ParseException(AssemblerToken token, String message, Throwable cause) {
    super(message, cause);
    this.token = token;
  }

  public AssemblerToken getToken() {
    return token;
  }
}
