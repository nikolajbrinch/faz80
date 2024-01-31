package dk.nikolajbrinch.assembler.linker;

import dk.nikolajbrinch.assembler.parser.statements.Statement;
import dk.nikolajbrinch.parser.BaseException;

public class LinkException extends BaseException {

  public LinkException(String message) {
    super(message);
  }

  public LinkException(String message, Throwable cause) {
    super(message, cause);
  }

}
