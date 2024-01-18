package dk.nikolajbrinch.assembler.compiler;

import dk.nikolajbrinch.assembler.parser.statements.Statement;

public class AssembleException extends RuntimeException {

  private final Statement statement;

  public AssembleException(Statement statement, String message) {
    super(message);
    this.statement = statement;
  }

  public AssembleException(Statement statement, String message, Throwable cause) {
    super(message, cause);
    this.statement = statement;
  }

  public Statement getStatement() {
    return statement;
  }
}
