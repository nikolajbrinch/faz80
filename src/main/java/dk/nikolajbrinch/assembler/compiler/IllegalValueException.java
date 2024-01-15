package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.ast.statements.Statement;

public class IllegalValueException extends AssembleException {

  public IllegalValueException(Statement statement, String message) {
    super(statement, message);
  }

  public IllegalValueException(Statement statement, String message, Throwable cause) {
    super(statement, message, cause);
  }
}
