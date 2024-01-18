package dk.nikolajbrinch.parser;

import dk.nikolajbrinch.assembler.parser.scanner.AssemblerToken;

public class ParseException extends RuntimeException {

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
