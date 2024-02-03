package dk.nikolajbrinch.faz80.assembler;

import dk.nikolajbrinch.faz80.parser.statements.Statement;

public class IllegalValueException extends AssembleException {

  public IllegalValueException(Statement statement, String message) {
    super(statement, message);
  }

  public IllegalValueException(Statement statement, String message, Throwable cause) {
    super(statement, message, cause);
  }
}
